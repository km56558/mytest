package com.pinyougou.user.service.impl;
import java.util.List;

import com.pinyougou.user.service.AddressService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.pojo.TbAddressExample.Criteria;

import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

	@Autowired
	private TbAddressMapper addressMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbAddress> findAll() {
		return addressMapper.selectByExample(null);
	}
	/**
	 * 增加
	 */
	@Override
	public Result add(TbAddress address) {
		addressMapper.insert(address);
		return new Result(true, "添加成功");
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbAddress address){
		addressMapper.updateByPrimaryKey(address);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbAddress findById(Long id){
		return addressMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 * @param ids
	 */
	@Override
	public void delete(Long ids) {

			addressMapper.deleteByPrimaryKey(ids);

	}
	//	根据用户查询地址
	@Override
	public List<TbAddress> findListByUserId(String userId) {
		TbAddressExample example = new TbAddressExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		return addressMapper.selectByExample(example);
	}

}
