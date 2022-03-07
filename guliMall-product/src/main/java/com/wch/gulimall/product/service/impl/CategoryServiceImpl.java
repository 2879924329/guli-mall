package com.wch.gulimall.product.service.impl;

import com.wch.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
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
        ).map((menu) ->{
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
     * @param category
     */
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
     * @param catelogId
     * @param path
     * @return
     */
    private List<Long> FindParentCateLogPath(Long catelogId, ArrayList<Long> path){
        //收集当前节点id
        path.add(catelogId);
        CategoryEntity id = this.getById(catelogId);
        if (id.getParentCid() != 0){
         FindParentCateLogPath(id.getParentCid(), path);
        }
        return path;
    }

    /**
     * 递归收集子菜单
     * @param root 父菜单
     * @param all 所有菜单
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){
       return all.stream().filter(categoryEntity -> {
           return categoryEntity.getParentCid().equals(root.getCatId());
       }).map(categoryEntity -> {
           //递归找子菜单
           categoryEntity.setChildren(getChildrens(categoryEntity, all));
           return categoryEntity;
           //菜单排序
       }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
    }
}