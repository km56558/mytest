package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.utils.IdWorker;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 增加
     */
    @Override
    public Result add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
        return new Result(true, "添加成功");
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void submitOrder(Long seckillId, String userId) {
        //从缓存中查询秒杀商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if (seckillGoods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("商品已抢购一空");
        }
        //扣减（redis）库存
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);//放回缓存
        //如果已经被秒光
        if (seckillGoods.getStockCount() == 0) {
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);//删除该商品缓存
        }

        //保存（redis）订单
        long orderId = idWorker.nextId();
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(orderId);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//状态
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }

    //根据用户ID从缓存查询订单id
    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

    }
    //支付成功保存订单
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:"+userId);
        TbSeckillOrder seckillOrder = searchOrderFromRedisByUserId(userId);
        if (seckillOrder == null){
            throw new RuntimeException("订单不存在");
        }
        //如果与传递过来的订单号不符
        if (seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单不相符");
        }
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        seckillOrder.setTransactionId(transactionId);//交易流水
        seckillOrderMapper.insert(seckillOrder);//保存到数据库
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //根据用户ID查询日志
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (seckillOrder != null &&seckillOrder.getId().longValue()==orderId.longValue()){
            redisTemplate.boundHashOps("seckillOrder").delete(userId);//删除缓存中的订单
        }
        //恢复库存
        //1.从缓存中提取秒杀商品
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
        if (seckillGoods!=null){
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
        }else {
            seckillGoods = new TbSeckillGoods();
            seckillGoods.setId(seckillOrder.getSeckillId());
            seckillGoods.setStockCount(1);
            //从数据库中根据seckillOrder.getSeckillId()查询秒杀商品...这里省略
        }
        redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(), seckillGoods);//存入缓存


    }

}
