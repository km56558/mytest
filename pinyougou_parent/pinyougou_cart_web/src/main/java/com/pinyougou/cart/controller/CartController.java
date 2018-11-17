package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.utils.CookieUtil;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 6000)
    private CartService cartService;

    //购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //通过工具类获取指定key的cookie值
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr == null || cartListStr.equals("")) {
            cartListStr = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr, Cart.class);
        if (username.equals("anonymousUser")) {//如果未登录

            return cartList_cookie;

        } else {
            //如果已经登陆
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);//从redis中提取购物车
            if (cartList_cookie.size() > 0) {
                //如果cookie中有购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清楚本地ciikie中购物车
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的购物车放入redis中
                cartService.saveCartListToRedis(username, cartList_redis);
            }
            return cartList_redis;
        }


    }

    //添加商品到购物车
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户：" + username);

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")) {
                //未登录
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                System.out.println("向cookie存入数据");
            } else {
                //如果已经登陆
                cartService.saveCartListToRedis(username, cartList);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
