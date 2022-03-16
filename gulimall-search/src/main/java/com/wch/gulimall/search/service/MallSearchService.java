package com.wch.gulimall.search.service;

import com.wch.gulimall.search.vo.SearchParamVo;
import com.wch.gulimall.search.vo.SearchResponse;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:15
 */
public interface MallSearchService {
    SearchResponse search(SearchParamVo searchParamVo);
}
