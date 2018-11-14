package com.pinyougou.user.controller;
import java.util.Date;
import java.util.List;

import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;

	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll() {
		return userService.findAll();
	}


	/**
	 * 返回全部列表
	 *
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult search(@RequestBody TbUser user, int page, int size) {
		return userService.findPage(user, page, size);
	}

	/**
	 * 增加
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String smscode) {
		boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);
		if(checkSmsCode==false){
			return new Result(false, "验证码输入错误！");
		}
		try {
			return userService.add(user);

		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	/**
	 * 修改
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user) {
		try {
			//如果成功添加
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}

	/**
	 * 获取实体
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/findById.do")
	public TbUser findById(Long id) {
		return userService.findById(id);
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids) {
		try {
			//如果成功添加
			userService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	@RequestMapping("/sendCode")
	public Result sendCode(String phone) {
		//判断手机号格式
		if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
			return new Result(false, "手机号格式不正确");
		}
		try {
			userService.createSmsCode(phone);//生成验证码
			return new Result(true, "验证码发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "验证码发送失败");
		}

	}
}
