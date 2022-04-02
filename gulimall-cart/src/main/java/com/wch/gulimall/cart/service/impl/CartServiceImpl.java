package com.wch.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wch.common.constant.CartConstant;
import com.wch.common.utils.R;
import com.wch.gulimall.cart.feign.ProductFeignService;
import com.wch.gulimall.cart.interceptor.CartInterceptor;
import com.wch.gulimall.cart.service.CartService;
import com.wch.gulimall.cart.vo.CartItemVo;
import com.wch.gulimall.cart.vo.CartVo;
import com.wch.gulimall.cart.vo.SkuInfoVo;
import com.wch.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:04
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 添加商品到购物车
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOptions = getCartOptions();
        String res = (String) cartOptions.get(skuId.toString());
        CartItemVo cartItemVo = new CartItemVo();
        //购物车原来无此商品
        if (StringUtils.isEmpty(res)) {
            //添加新商品到购物车
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                //远程查询要添加的商品的信息
                R skuInfo = productFeignService.info(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItemVo.setChecked(true);
                cartItemVo.setCount(num);
                cartItemVo.setImage(data.getSkuDefaultImg());
                cartItemVo.setTitle(data.getSkuTitle());
                cartItemVo.setSkuId(skuId);
                cartItemVo.setPrice(data.getPrice());
            }, threadPoolExecutor);

            //远程查询sku的组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttr(skuSaleAttrValues);
            }, threadPoolExecutor);

            CompletableFuture.allOf(getSkuInfo, getSkuSaleAttrValues).get();
            String toJSONString = JSON.toJSONString(cartItemVo);
            cartOptions.put(skuId.toString(), toJSONString);
            return cartItemVo;
        } else {
            // 购物车有此商品，修改数量即可
            CartItemVo itemVo = JSON.parseObject(res, CartItemVo.class);
            itemVo.setCount(itemVo.getCount() + num);
            cartOptions.put(skuId.toString(), JSON.toJSONString(itemVo));
            return itemVo;
        }
    }

    /**
     * 获取购物车中的购物项
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOptions = getCartOptions();
        String result = (String) cartOptions.get(skuId.toString());
        return JSON.parseObject(result, CartItemVo.class);
    }

    /**
     * 获取整个购物车的内容
     *
     * @return
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        //1， 区分用户的登录状态
        //快速得到用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        if (!StringUtils.isEmpty(userInfoTo)) {
            //登录
            //如果临时购物车的数据还没有进行合并
            List<CartItemVo> tempCartItem = getCartItem(tempCartKey);
            if (!CollectionUtils.isEmpty(tempCartItem)) {
                //临时购物车有数据，需要进行合并
                for (CartItemVo cartItemVo : tempCartItem) {
                    addToCart(cartItemVo.getSkuId(), cartItemVo.getCount());
                }
                //清空临时购物车数据
                cleanCart(tempCartKey);
            }
            //获取登录后的购物车数据(包含所有数据，登录前和登录后的)
            List<CartItemVo> cartItem = getCartItem(cartKey);
            cartVo.setCartItemVos(cartItem);
        } else {
            //木有登录
            //获取临时购物车的所有购物项
            List<CartItemVo> cartItem = getCartItem(tempCartKey);
            cartVo.setCartItemVos(cartItem);
        }
        return cartVo;
    }

    /**
     * 清空购物车数据
     *
     * @param cartKey
     */
    @Override
    public void cleanCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    /**
     * 勾选购物项
     *
     * @param skuId
     * @param checked
     */
    @Override
    public void checkItem(Long skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> cartOptions = getCartOptions();
        CartItemVo item = getCartItem(skuId);
        item.setChecked(checked == 1);
        String s = JSON.toJSONString(item);
        cartOptions.put(skuId.toString(), s);
    }

    /**
     * 修改购物项数量
     *
     * @param skuId
     * @param num
     */
    @Override
    public void changeCountItem(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOptions = getCartOptions();
        cartOptions.put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    /**
     * 删除购物车选项
     *
     * @param skuId
     */
    @Override
    public void deleteItem(Long skuId) {
        getCartOptions().delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getCurrentCartItem() {
        Long userId = CartInterceptor.threadLocal.get().getUserId();
        String cartKey = CartConstant.CART_PREFIX + userId;
        if (ObjectUtils.isEmpty(userId)) {
            return Collections.emptyList();
        }
        /**
         * 获取被选中的购物项
         */
        return getCartItem(cartKey).stream().filter(CartItemVo::getChecked).map(item -> {
            //获取最新价格
            R r = productFeignService.getPrice(item.getSkuId());
            String price = (String) r.get("data");
            item.setPrice(new BigDecimal(price));
            return item;
        }).collect(Collectors.toList());
    }

    private List<CartItemVo> getCartItem(String cartKey) {
        BoundHashOperations<String, Object, Object> cartOptions = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = cartOptions.values();
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream().map(obj -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 获取要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOptions() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey;
        if (!StringUtils.isEmpty(userInfoTo.getUserId())) {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(cartKey);
    }
}
