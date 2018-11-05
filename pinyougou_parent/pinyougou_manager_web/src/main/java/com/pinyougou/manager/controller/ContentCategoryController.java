package com.pinyougou.manager.controller;
import java.util.List;

import com.pinyougou.content.service.ContentCategoryService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbContentCategory;
import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {

	@Reference
	private ContentCategoryService contentCategoryService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbContentCategory> findAll(){			
		return contentCategoryService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult  search(@RequestBody TbContentCategory contentCategory,int page,int size){
		return contentCategoryService.findPage(contentCategory,page,size);
	}
	
	/**
	 * 增加
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbContentCategory contentCategory){
		try {
		//如果成功添加
		return contentCategoryService.add(contentCategory);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param contentCategory
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbContentCategory contentCategory){
		try {
		//如果成功添加
		contentCategoryService.update(contentCategory);
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
	public TbContentCategory findById(Long id){
		return contentCategoryService.findById(id);
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
		contentCategoryService.delete(ids);
		return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}
	
}
