package com.wch.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wch.gulimall.product.service.CategoryBrandRelationService;
import com.wch.gulimall.product.vo.web.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.product.dao.CategoryDao;
import com.wch.gulimall.product.entity.CategoryEntity;
import com.wch.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有分类
        List<CategoryEntity> categoryEntityList = categoryDao.selectList(null);
        //组成成父子的树形结构
        // 1)找到所有的一级分类
        return categoryEntityList.stream().filter(categoryEntity ->
                        categoryEntity.getParentCid() == 0
                //递归收集子菜单
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, categoryEntityList));
            return menu;
            //排序
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO   检查当前当前删除的菜单是否被别的地方引用
        //使用逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据提供的某个分类id，查出他的完整的分类id路径 [父路径，子路径，孙子路径]
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCateLogPath(Long catelogId) {
        ArrayList<Long> path = new ArrayList<>();
        List<Long> parentPath = FindParentCateLogPath(catelogId, path);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     *   @CacheEvict : 缓存失效模式的使用
     *修改目录的时候删除缓存, 指定要删除的缓存区
     *
     * @Caching ： 同时进行多种缓存操作
     *
     *  @CacheEvict(value = "category", allEntries = true) 删除某个分区的所有内容
     * @CachePut : 双写模式
     *  存储一个业务类型的数据，都可以指定成同一个分区，不在配置文件指定key-prefix，默认分区名就是缓存的名字
     * @param category
     */


//    @CacheEvict(value = "category", allEntries = true)
    @Caching(evict = {@CacheEvict(value = "category", key = "'getLevelFirstCategorys'"),
            @CacheEvict(value = "category", key = "'getCatelogJson'")   } )
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }


    /**
     * 递归收集路径id
     *
     * @param catelogId
     * @param path
     * @return
     */
    private List<Long> FindParentCateLogPath(Long catelogId, ArrayList<Long> path) {
        //收集当前节点id
        path.add(catelogId);
        CategoryEntity id = this.getById(catelogId);
        if (id.getParentCid() != 0) {
            FindParentCateLogPath(id.getParentCid(), path);
        }
        return path;
    }

    /**
     * 递归收集子菜单
     *
     * @param root 父菜单
     * @param all  所有菜单
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        return all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //递归找子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
            //菜单排序
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
    }


    /**
     * 前台页面
     * 查询所有的一级分类
     *     @Cacheable ： 代表当前的结果需要缓存，如果缓存中有，方法不必调用，缓存中没有则调用方法，
     *       最后将解雇写入缓存
     * 使用SpringCache以后 ，需要指定要放到哪个名字的缓存（缓存分区）
     * 1，默认行为
     *    1）如果缓存中有，方法不必调用
     *    2）key是默认自动生成（category::SimpleKey []）
     *    3）缓存的value：默认使用jdk序列化机制，存入redis
     *    4）默认过期时间是-1
     * 自定义操作，
     *    指定key，key,接收一个SpEL表达式
     *    指定过期时间，配置文件中设置
     *    将数据保存为json格式
     * @return
     */
    @Cacheable(value = "category", key = ("#root.methodName"),sync = true)
    @Override
    public List<CategoryEntity> getLevelFirstCategorys() {
        System.out.println("方法调用");
        long l = System.currentTimeMillis();
        List<CategoryEntity> list = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("耗时：" + (System.currentTimeMillis() - l));
        return list;
    }


    /**
     * 使用注解加入缓存
     * @return
     */
    @Cacheable(value = "category", key = ("#root.methodName"))
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);
        //查询所有一级分类
        List<CategoryEntity> levelFirstCategorys = getParentCid(categoryEntityList, 0L);
        //封装数据
        return levelFirstCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParentCid(categoryEntityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //找到当前二级分类的三级分类
                    List<CategoryEntity> level3List = getParentCid(categoryEntityList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3List)) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3List.stream().map(l3 -> {
                            //封装成指定格式
                            return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
    }

    /**
     * 查询三级目录，整合redis缓存
     *
     * @return
     */
    //@Override
    public Map<String, List<Catelog2Vo>> getCatelogJson2() {
        // TODO 产生堆外内存溢出
        // springboot2.0以后默认使用lettuce作为操作redis的客户端，它使用netty进行网络通信
        //lettuce的bug导致堆外内存溢出,设置的jvm参数-Xmx300m,netty如果没有指定堆外内存，默认-Xmx300m
        //可以通过-Dio.netty.maxDirectMemory进行设置
        //解决方案：不能使用-Dio.netty.maxDirectMemory只去调大堆外内存
        // 1，升级lettuce客户端， 2，切换使用jedis
        //lettuce jedis都是操作redis的最底层的客户端，spring再次封装redisTemplate

        /**
         * 1， 空结果缓存 ：解决缓存穿透
         * 2， 设置过期时间（加随机值） 解决缓存雪崩
         * 3， 加锁 解决缓存击穿
         */
        //1, 加入缓存逻辑
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //2,缓存中没有数据， 查数据库
            System.out.println("缓存不命中 ，将要查询数据库");
            return getCatalogJsonFromDBWithRedissonLock();
        }
        //给缓存中放json，返回的是我们需要的，逆转为能用的对象类型
        System.out.println("缓存命中，直接返回");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
    }

    /**
     * 使用redisson分布式锁
     *
     * 缓存一致性：
     *   1）双写模式
     *   2）失效模式
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        //1， 占分布式锁
        // 锁的粒度，越细越好。具体缓存的是某个数据，比如11-号商品，则锁：product-11-lock,12号商品，product-12-lock
        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDB;
        try {
            dataFromDB = getDataFromDB();
        } finally {
           lock.unlock();
        }

        return dataFromDB;
    }


    /**
     * 使用redis的自带锁
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithRedisLock() {
        /**
         * 优化
         * 1， 将数据库的多次查询变为一次
         */
        String uuid = UUID.randomUUID().toString();
        //获取锁的同时加过期时间
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            log.info("获取分布式锁成功！");
            System.out.println("获取分布式锁成功！");
            //执行业务
            Map<String, List<Catelog2Vo>> dataFromDB = null;
            try {
                dataFromDB = getDataFromDB();

            } finally {
                //保证删锁的原子性，lua脚本实现
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                        Collections.singletonList("lock"), uuid);
            }
            //删锁之前，为防止误删，先查，再删
         /*   String value = stringRedisTemplate.opsForValue().get("lock");
            if (uuid.equals(value)){
                //释放锁
                stringRedisTemplate.delete("lock");
            }*/
            return dataFromDB;
        } else {
            log.info("获取分布式锁失败，等待重试！");
            System.out.println("获取分布式锁失败，等待重试！");
            //加锁失败，自旋重试
            //休眠100ms
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatelogJsonFromDBWithRedisLock();
        }

    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        //1, 判断
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            //2,缓存不为null，直接返回
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        System.out.println("查询了数据库");
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);
        //查询所有一级分类
        List<CategoryEntity> levelFirstCategorys = getParentCid(categoryEntityList, 0L);
        //封装数据
        Map<String, List<Catelog2Vo>> map = levelFirstCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParentCid(categoryEntityList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (!CollectionUtils.isEmpty(categoryEntities)) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //找到当前二级分类的三级分类
                    List<CategoryEntity> level3List = getParentCid(categoryEntityList, l2.getCatId());
                    if (!CollectionUtils.isEmpty(level3List)) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3List.stream().map(l3 -> {
                            //封装成指定格式
                            return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        //3， 查询到的数据重新放入redis, 缓存中存的数据是json字符串(跨语言跨平台兼容)
        String jsonString = JSON.toJSONString(map);
        stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
        return map;
    }


    /**
     * @return 前台封装二、三级数据(未使用缓存的查询三级目录)
     * <p>
     * 使用本地锁
     */
    public Map<String, List<Catelog2Vo>> getCatelogJsonFromDBWithLocalLock() {

        /**
         * 加锁, synchronized (this):springboot中的所有组件都是单例的
         *   //得到锁以后，应该再去缓存中确定一次，如果没有才需要查询
         *    synchronized (this) {
         *
         *
         *         }
         *
         *   问题：本地锁（synchronized， lock）只能锁住当前进程，想要锁住所有，需要分布式锁
         *
         */

        /**
         * 优化
         * 1， 将数据库的多次查询变为一次
         */
        synchronized (this) {
            //1, 判断
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJson)) {
                //2,缓存不为null，直接返回
                return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
            }
            List<CategoryEntity> categoryEntityList = baseMapper.selectList(null);
            //查询所有一级分类
            List<CategoryEntity> levelFirstCategorys = getParentCid(categoryEntityList, 0L);
            //封装数据
            return levelFirstCategorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                //每一个的一级分类，查到这个一级分类的二级分类
                List<CategoryEntity> categoryEntities = getParentCid(categoryEntityList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (!CollectionUtils.isEmpty(categoryEntities)) {
                    catelog2Vos = categoryEntities.stream().map(l2 -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                        //找到当前二级分类的三级分类
                        List<CategoryEntity> level3List = getParentCid(categoryEntityList, l2.getCatId());
                        if (!CollectionUtils.isEmpty(level3List)) {
                            List<Catelog2Vo.Catelog3Vo> collect = level3List.stream().map(l3 -> {
                                //封装成指定格式
                                return new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect);
                        }
                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                //3， 查询到的数据重新放入redis, 缓存中存的数据是json字符串(跨语言跨平台兼容)
                String jsonString = JSON.toJSONString(catelog2Vos);
                stringRedisTemplate.opsForValue().set("catalogJson", jsonString, 1, TimeUnit.DAYS);
                return catelog2Vos;
            }));
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> categoryEntityList, Long parentCid) {
        return categoryEntityList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }
}