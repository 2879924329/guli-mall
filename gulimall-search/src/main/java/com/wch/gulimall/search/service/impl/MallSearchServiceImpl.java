package com.wch.gulimall.search.service.impl;

import com.wch.gulimall.search.service.MallSearchService;
import com.wch.gulimall.search.vo.SearchParamVo;
import com.wch.gulimall.search.vo.SearchResponse;
import org.springframework.stereotype.Service;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:15
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    /**
     * 商品前台检索，自动将页面传过来的请求参数封装成对象
     * @param searchParamVo 搜索栏传来的参数
     * @return 根据页面传递过来的信息去检索返回结果
     */
    @Override
    public SearchResponse search(SearchParamVo searchParamVo) {

        return null;
    }
}
