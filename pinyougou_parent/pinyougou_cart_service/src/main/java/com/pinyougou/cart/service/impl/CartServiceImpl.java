package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据商品sku id查询sku商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")) {
            throw new RuntimeException("商品状态无效");
        }
        //2.获取商家id
        String sellerId = item.getSellerId();
        //3.判断购物车列表中是否已经存在该商家(根据商家id)
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.如果购物车列表中已经存在该商家
        if (cart != null) {

            //4.1 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem != null) {

                //4.1.1 如果明细列表中已经存在该商品,则数量加上num,更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);//移除购物车明细
                }
                //如果移除后cart的明细数量为0，则将cart移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            } else {

                //4.1.2 如果明细列表中不存在该商品.则将该商品添加到明细列表
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }

        }
        //5.如果购物车列表中不存在该商家
        else {
            //5.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item, num);
            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            //5.2 将新建的购物车对象添加到购物车列表中
            cartList.add(cart);
        }


        return cartList;
    }
    //从redis中获取购物车列表

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....." + username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        return cartList;
    }

    //将购物车列表保存到redis
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....." + username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);

    }

    //合并购物车
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
                cartList1 = addGoodsToCartList(cartList1, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }

        return cartList1;
    }

    //创建订单明细
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    //根据商品明细ID查询
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList != null || orderItemList.size() > 0) {
            for (TbOrderItem tbOrderItem : orderItemList) {
                if (tbOrderItem.getItemId().longValue() == itemId.longValue()) {
                    return tbOrderItem;
                }
            }
        }
        return null;

    }

    //根据商家ID查询购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null || cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }

            }
        }
        return null;
    }

}
