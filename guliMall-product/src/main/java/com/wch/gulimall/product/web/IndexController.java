package com.wch.gulimall.product.web;

import com.wch.gulimall.product.entity.CategoryEntity;
import com.wch.gulimall.product.service.CategoryService;
import com.wch.gulimall.product.vo.web.Catelog2Vo;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/11 11:08
 *
 * 商城首页
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/", "index.html"})
    public String indexHtml(Model model){
        //查询一级分类
        List<CategoryEntity> list = categoryService.getLevelFirstCategorys();
        model.addAttribute("categorys", list);
        //视图解析器进行拼串
        //classpath:/templates/ + 返回值 + .html
        return "index";
    }
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCategoryJsonData(){
        return categoryService.getCatelogJson();
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //获取一把锁，只要锁的名字一样，就是同一把锁
        //redisson解决了两个问题，
          //1）锁的自动续期，如果业务超长，运行期间自动给锁续期30s，不用担心业务时间长，锁自动供货期
          //2）加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s内自动释放
        RLock rLock = redissonClient.getLock("myLock");
        //加锁，阻塞式等待
        //rLock.lock();
        //过期时间自己设置10s，自动解锁时间一定要大于业务的执行时间，指定了过期时间，锁时间到了以后不会自动续期
        //如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时时间就是我们指定的时间
        //如果我们没有传递锁的超时时间，就使用30 * 1000（getLockWatchdogTimeout()看门狗默认时间），
                // 只要占锁成功，就会启动一个定时任务，重新给锁设置时间，时间是默认时间
                 // 1/3的默认时间（10s）后自动续期

        //最佳实战    rLock.lock(10, TimeUnit.SECONDS);省掉了整个续期操作。手动解锁
        rLock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功" + Thread.currentThread().getId());
           Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("释放锁" + Thread.currentThread().getId());
            rLock.unlock();
        }
        return "hello!";
    }


    /**
     * 保证一定能读到最新数据，写锁是一个排他锁（互斥锁），读锁是一个共享锁，写锁没有释放，读锁就必须等待
     *
     * 读 + 写：等待读锁释放
     * 写 + 写：阻塞状态
     * 写 + 读：等待写锁释放
     * 读 + 读：相当于无锁，并发读只会在redis中记录好所有当前的读锁，他们会同时加锁成功
     *
     * 只要有写的存在，都必须等待
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String write(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = readWriteLock.writeLock();
        try {
            //1, 该数据加写锁，读数据加读锁
            rLock.lock();
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeKey", s);
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }
    @ResponseBody
    @GetMapping("/read")
    public String read(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            s = stringRedisTemplate.opsForValue().get("writeKey");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }



}
