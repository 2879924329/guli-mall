package com.wch.gulimall.search.service;

import com.wch.common.to.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 22:22
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
