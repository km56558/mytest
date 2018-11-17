package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.utils.IdWorker;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;

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
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsMapper.selectByExample(null);
    }

    /**
     * 增加
     */
    @Override
    public Result add(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.insert(seckillGoods);
        return new Result(true, "添加成功");
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findById(Long id) {
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }

        }

        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    //返回正在秒杀的商品
    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoods == null || seckillGoods.size() <= 0) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();
            Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过
            criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
            criteria.andEndTimeGreaterThanOrEqualTo(new Date());//结束时间大于当前时间
            criteria.andStockCountGreaterThan(0);//剩余库存大于0
            seckillGoods = seckillGoodsMapper.selectByExample(example);
            //将商品装入缓存
            System.out.println("将秒杀商品列表装入缓存");
            for (TbSeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
            }
        }
        return seckillGoods;
    }

    @Override
    public TbSeckillGoods findOneFromRedis(Long id) {
        return  (TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
    }



}
