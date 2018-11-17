package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.utils.IdWorker;
import com.pinyougou.vo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbPayLogMapper payLogMapper;


	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}
	/**
	 * 增加
	 */
	@Override
	public Result add(TbOrder order) {
		//得到购物车数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get( order.getUserId() );
		List<String> orderIdList=new ArrayList();//订单ID列表
		double total_money=0;//总金额 （元）
		for (Cart cart : cartList) {
			long orderId  = idWorker.nextId();
			System.out.println("sellerId:"+cart.getSellerId());
			TbOrder tbOrder = new TbOrder();
			tbOrder.setOrderId(orderId);//订单id
			tbOrder.setUserId(order.getUserId());//用户名
			tbOrder.setPaymentType(order.getPaymentType());//支付类型
			tbOrder.setStatus("1");//状态:未付款
			tbOrder.setCreateTime(new Date());//订单创建日期
			tbOrder.setUpdateTime(new Date());//订单更新日期
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//地址
			tbOrder.setReceiverMobile(order.getReceiverMobile());//手机号
			tbOrder.setReceiver(order.getReceiver());//收货人
			tbOrder.setSourceType(order.getSourceType());//订单来源
			tbOrder.setSellerId(cart.getSellerId());
			//循环购物车明细
			double money=0;
			for (TbOrderItem tbOrderItem : cart.getOrderItemList()) {
				tbOrderItem.setId(idWorker.nextId());
				tbOrderItem.setOrderId(orderId);//订单ID
				tbOrderItem.setSellerId(cart.getSellerId());
				money+=tbOrderItem.getTotalFee().doubleValue();//每个明细总计金额累加
				orderItemMapper.insert(tbOrderItem);
			}
			tbOrder.setPayment(new BigDecimal(money));
			orderMapper.insert(tbOrder);
			orderIdList.add(orderId+"");//将顶单ID添加到订单列表
			total_money += money; //累加总金额
		}
		if ("1".equals(order.getPaymentType())){
			//如果是微信支付
			TbPayLog payLog = new TbPayLog();
			payLog.setOutTradeNo(idWorker.nextId()+"");//支付订单编号
			payLog.setCreateTime(new Date());//订单支付创建时间
			payLog.setPayType("1");//支付类型
			payLog.setTotalFee((long)(total_money*100));//*100得到单位为分
			payLog.setTradeState("0");//支付状态
			payLog.setUserId(order.getUserId());//用户ID
			String orderIdListStr = orderIdList.toString().replace("[", "").replace("]", "");
			payLog.setOrderList(orderIdListStr);//订单编号列表

			payLogMapper.insert(payLog);
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);//放入缓存


		}

		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		return new Result(true, "添加成功");
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findById(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transaction_id) {
		//1.修改支付日志状态
		TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		payLog.setTransactionId(transaction_id);//交易号
		payLog.setPayTime(new Date());//支付时间
		payLog.setTradeState("1");//已支付
		payLogMapper.updateByPrimaryKey(payLog);
		//从日志获取订单编号修改订单状态
		String orderListStr = payLog.getOrderList();
		String[] orderIds = orderListStr.split(",");
		for (String orderId : orderIds) {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
			if (tbOrder!=null){
				tbOrder.setStatus("2");//已付款
				orderMapper.updateByPrimaryKey(tbOrder);
			}
		}
		//清除redis缓存数据
		redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
	}

}
