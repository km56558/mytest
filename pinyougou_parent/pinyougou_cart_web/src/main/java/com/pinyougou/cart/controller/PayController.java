package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    private OrderService orderService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询支付日志
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        //判断支付日志存在
        if (tbPayLog != null)
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee() + "");
        return new HashMap();
    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        int x = 0;
        while (true) {

            Map map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null)
                return new Result(false, "支付出错");
            if (map.get("trade_state").equals("SUCCESS")) {
                //如果成功
                //修改订单状态
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id") + "");
                return new Result(true, "支付成功");
            }
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            x++;
            if (x>100)
                return new Result(false,"二维码已过期");
        }
    }

}
