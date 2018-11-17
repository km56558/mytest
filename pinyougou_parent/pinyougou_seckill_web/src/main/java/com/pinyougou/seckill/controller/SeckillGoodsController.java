package com.pinyougou.seckill.controller;
import java.util.List;

import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

	@Reference(timeout=10000)
	private SeckillGoodsService seckillGoodsService;

	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillGoods> findAll(){			
		return seckillGoodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult  search(@RequestBody TbSeckillGoods seckillGoods,int page,int size){
		return seckillGoodsService.findPage(seckillGoods,page,size);
	}
	
	/**
	 * 增加
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillGoods seckillGoods){
		try {
		//如果成功添加
		return seckillGoodsService.add(seckillGoods);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillGoods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillGoods seckillGoods){
		try {
		//如果成功添加
		seckillGoodsService.update(seckillGoods);
		return new Result(true,"修改成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findById.do")
	public TbSeckillGoods findById(Long id){
		return seckillGoodsService.findById(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
		//如果成功添加
		seckillGoodsService.delete(ids);
		return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}

	// 当前秒杀的商品
	@RequestMapping("/findList")
	public List<TbSeckillGoods> findList(){
		return seckillGoodsService.findList();
	}

	@RequestMapping("/findOneFromRedis")
	public TbSeckillGoods findOneFromRedis(Long id){
		return seckillGoodsService.findOneFromRedis(id);
	}

	@RequestMapping("/submitOrder")
	public Result submitOrder(Long seckillId){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if("anonymousUser".equals(userId)){//如果未登录
			return new Result(false, "用户未登录");
		}
		try {
			seckillOrderService.submitOrder(seckillId, userId);
			return new Result(true, "提交成功");
		}catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交失败");
		}

	}
	
}
