package com.wch.gulimall.search.controller;

import com.wch.common.exception.Code;
import com.wch.common.to.SkuEsModel;
import com.wch.common.utils.R;
import com.wch.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 22:20
 */
@Slf4j
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;
    @RequestMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){
        try {
            productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            return R.error(Code.PRODUCT_UP_EXCEPTION.getCode(), Code.PRODUCT_UP_EXCEPTION.getMessage());
        }
        return R.ok();
    }
}
