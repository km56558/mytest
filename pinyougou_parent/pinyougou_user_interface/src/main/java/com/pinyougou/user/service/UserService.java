package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();

	
	
	/**
	 * 增加
	*/
	public Result add(TbUser user);
	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findById(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum, int pageSize);

	void createSmsCode(String phone);

	boolean  checkSmsCode(String phone,String code);
	
}
