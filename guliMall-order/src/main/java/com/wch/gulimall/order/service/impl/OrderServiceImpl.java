package com.wch.gulimall.order.service.impl;

import com.wch.common.to.MemberEntityTo;
import com.wch.gulimall.order.feign.CartFeignService;
import com.wch.gulimall.order.feign.MemberFeignService;
import com.wch.gulimall.order.interceptor.LoginUserInterceptor;
import com.wch.gulimall.order.to.MemberAddressTo;
import com.wch.gulimall.order.to.OrderItemVo;
import com.wch.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.order.dao.OrderDao;
import com.wch.gulimall.order.entity.OrderEntity;
import com.wch.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private CartFeignService cartFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 订单确认页返回需要用的数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        MemberEntityTo memberEntityTo = LoginUserInterceptor.loginUser.get();
        Long id = memberEntityTo.getId();
        //远程查询收获列表
        List<MemberAddressTo> address = memberFeignService.getAddress(id);
        orderConfirmVo.setAddress(address);
        //远程查询购物车购物项数据
        List<OrderItemVo> cartItems = cartFeignService.getCartItems();
        orderConfirmVo.setItems(cartItems);
        //查询用户积分
        Integer integration = memberEntityTo.getIntegration();
        orderConfirmVo.setIntegration(integration);
        return orderConfirmVo;
    }

}