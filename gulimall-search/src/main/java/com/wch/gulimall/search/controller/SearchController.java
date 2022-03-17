package com.wch.gulimall.search.controller;

import com.wch.gulimall.search.service.MallSearchService;
import com.wch.gulimall.search.vo.SearchParamVo;
import com.wch.gulimall.search.vo.SearchResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 20:53
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listSearch(SearchParamVo searchParamVo, Model model) {
        SearchResultResponse result = mallSearchService.search(searchParamVo);
        model.addAttribute("result", result);
        return "list";
    }
}
