package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.vo.SpecificationVo;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;


import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}
	/**
	 * 增加
     * @param specification
     */
	@Override
	public Result add(SpecificationVo specification) {
		TbSpecification tbSpecification = specification.getSpecification();
		//插入规格
		specificationMapper.insert(tbSpecification);
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			//设置当前规格ID
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			//插入规格选项
			specificationOptionMapper.insert(tbSpecificationOption);
		}
		return new Result(true, "添加成功");
	}

	
	/**
	 * 修改
	 * @param specification
	 */
	@Override
	public void update(SpecificationVo specification){
		//更新规格表
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
		//删除规格选项表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		Long specId = specification.getSpecification().getId();
		criteria.andSpecIdEqualTo(specId);
		specificationOptionMapper.deleteByExample(example);
		//添加规格选项表
		for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
			//设置规格选项所属规格id
			specificationOption.setSpecId(specId);
			//添加规格选项
			specificationOptionMapper.insert(specificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public SpecificationVo findById(Long id){
		SpecificationVo vo = new SpecificationVo();
		//查询规格
		vo.setSpecification(specificationMapper.selectByPrimaryKey(id));
		//查询该规格选项列表
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		vo.setSpecificationOptionList(specificationOptionMapper.selectByExample(example));
		return vo;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//删除规格
			specificationMapper.deleteByPrimaryKey(id);
			//删除规格选项
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
