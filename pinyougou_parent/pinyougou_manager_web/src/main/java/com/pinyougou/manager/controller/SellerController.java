package com.pinyougou.manager.controller;
import java.util.List;

import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){			
		return sellerService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult  search(@RequestBody TbSeller seller,int page,int size){
		return sellerService.findPage(seller,page,size);
	}
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller seller){
		try {
		//如果成功添加
		return sellerService.add(seller);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller seller){
		try {
		//如果成功添加
		sellerService.update(seller);
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
	public TbSeller findById(String id){
		return sellerService.findById(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(String[] ids){
		try {
		//如果成功添加
		sellerService.delete(ids);
		return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}
 @RequestMapping("/updateStatus")
	//商家审核
	public Result updateStatus(String sellerId, String status){
		try {
			sellerService.updateStatus(sellerId,status);
			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
	
}
