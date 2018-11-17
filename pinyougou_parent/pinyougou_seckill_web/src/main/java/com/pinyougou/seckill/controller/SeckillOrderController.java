package com.pinyougou.seckill.controller;
import java.util.List;

import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillOrder> findAll(){			
		return seckillOrderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult  search(@RequestBody TbSeckillOrder seckillOrder,int page,int size){
		return seckillOrderService.findPage(seckillOrder,page,size);
	}
	
	/**
	 * 增加
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillOrder seckillOrder){
		try {
		//如果成功添加
		return seckillOrderService.add(seckillOrder);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillOrder seckillOrder){
		try {
		//如果成功添加
		seckillOrderService.update(seckillOrder);
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
	public TbSeckillOrder findById(Long id){
		return seckillOrderService.findById(id);
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
		seckillOrderService.delete(ids);
		return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}
	
}
