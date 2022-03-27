package com.wch.gulimall.cart.service;

import com.wch.gulimall.cart.vo.CartItemVo;
import com.wch.gulimall.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:03
 */
public interface CartService {
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    CartVo getCart() throws ExecutionException, InterruptedException;

    void cleanCart(String cartKey);

    void checkItem(Long skuId, Integer checked);

    void changeCountItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
