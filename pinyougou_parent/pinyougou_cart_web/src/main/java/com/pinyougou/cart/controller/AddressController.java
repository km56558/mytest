package com.pinyougou.cart.controller;
import java.util.List;

import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbAddress> findAll(){			
		return addressService.findAll();
	}
	

	
	/**
	 * 增加
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress address){
		try {
		//如果成功添加
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(name);
            return addressService.add(address);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress address){
		try {
		//如果成功添加
		addressService.update(address);
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
	public TbAddress findById(Long id){
		return addressService.findById(id);
	}
	
	//批量删除

	@RequestMapping("/delete")
	public Result delete(Long id){
		try {
		//如果成功添加
		addressService.delete(id);
		return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}

	@RequestMapping("/findListByLoginUser")
	public List<TbAddress> findListByLoginUser(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findListByUserId(userId);

	}
	
}
