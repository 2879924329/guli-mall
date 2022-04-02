package com.wch.gulimall.cart.controller;

import com.wch.common.constant.AuthServerConstant;
import com.wch.gulimall.cart.interceptor.CartInterceptor;
import com.wch.gulimall.cart.service.CartService;
import com.wch.gulimall.cart.vo.CartItemVo;
import com.wch.gulimall.cart.vo.CartVo;
import com.wch.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 20:07
 */
@Controller
public class CartController {


    @Autowired
    private CartService cartService;

    /**
     * 浏览器有一个cookie：user-key， 标识用户身份，一个月后过期
     * 如果第一次使用购物车功能，都会自动给一个临时的用户身份
     * 浏览器以后保存，每次访问都带上这个cookie
     *
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(Model model) throws ExecutionException, InterruptedException {
        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cartList";
    }

    /**
     * 添加购物车
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        //防止重复请求，使用重定向
        return "redirect:http://cart.guli-mall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到购物车添加成功页面
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面，再次查询购物车数据即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItemVo);
        return "success";
    }

    /**
     * 购物车商品选项的的选中状态
     *
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("checked") Integer checked) {
        cartService.checkItem(skuId, checked);
        return "redirect:http://cart.guli-mall.com/cart.html";
    }

    /**
     * 购物车列表中的购物项的改变数量
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) {
        cartService.changeCountItem(skuId, num);
        return "redirect:http://cart.guli-mall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.guli-mall.com/cart.html";
    }

    /**
     * 获取当前用户的所有要结算的购物车列表
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/current/cart-items")
    public List<CartItemVo> getCartItems() {
        return cartService.getCurrentCartItem();
    }
}
