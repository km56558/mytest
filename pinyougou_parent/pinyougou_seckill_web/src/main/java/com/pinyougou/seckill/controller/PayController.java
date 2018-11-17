package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询秒杀订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断订单是否存在
        if (seckillOrder != null) {
            long fen = (long) seckillOrder.getMoney().doubleValue() * 100;
            return weixinPayService.createNative(seckillOrder.getId() + "", fen + "");
        }
        return new HashMap();
    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result = null;
        int x = 0;
        while (true) {

            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null)
                return new Result(false, "支付出错");
            if (map.get("trade_state").equals("SUCCESS")) {
                //如果成功
                //修改订单状态
                //orderService.updateOrderStatus(out_trade_no, map.get("transaction_id") + "");
                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            x++;
            if (x > 100) {
                Map<String, String> payresult = weixinPayService.closePay(out_trade_no);
                if (!"SUCCESS".equals(payresult.get("result_code"))) {//如果返回结果是正常关闭
                    if ("ORDERPAID".equals(payresult.get("err_code"))) {
                        seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                        return new Result(true, "支付成功");
                    }
                }

                if (result.isSuccess() == false) {
                    System.out.println("超时，取消订单");
                    //2.调用删除
                    seckillOrderService.deleteOrderFromRedis(userId, Long.valueOf(out_trade_no));
                    return new Result(false,"超时");
                }
            }
        }
    }
}




