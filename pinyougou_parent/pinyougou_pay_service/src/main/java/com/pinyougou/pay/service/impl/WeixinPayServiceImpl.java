package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    //生成支付二维码
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> param=new HashMap();//创建参数
        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("out_trade_no",out_trade_no);
        param.put("nonce_str",WXPayUtil.generateNonceStr());
        param.put("body","品优购");
        param.put("total_fee",total_fee);
        param.put("spbill_create_ip","127.0.0.1");
        param.put("notify_url","http://test.itcast.cn");
        param.put("trade_type","NATIVE");
        try {
            //生成要发送的xml
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(paramXml);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            //获得结果
            String content = httpClient.getContent();
            System.out.println(content);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            Map<String, String> map=new HashMap<>();
            map.put("code_url",resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }
//查询订单状态
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("out_trade_no",out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            //获取结果
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            System.out.println(map);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
