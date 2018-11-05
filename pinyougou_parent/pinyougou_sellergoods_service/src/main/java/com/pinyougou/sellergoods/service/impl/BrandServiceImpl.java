package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    //查找全部品牌类别
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    //获取品牌分页所需结果
    @Override
    public PageResult findPage(TbBrand brand, Integer page, Integer size) {
        PageHelper.startPage(page, size);//设置分页
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        if (brand != null) {
            if (brand.getName() != null && brand.getName().length() > 0) {
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if (brand.getFirstChar() != null && brand.getFirstChar().length() > 0) {
                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
        }
        //将查询结果通过分页插件转换
        Page<TbBrand> info = (Page<TbBrand>) (brandMapper.selectByExample(example));

        //返回PageResult
        return new PageResult(info.getTotal(), info.getResult());
    }

    //品牌添加
    @Override
    public Result add(TbBrand brand) {
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(brand.getName());
        //查询名称是否已存在
        int count = brandMapper.countByExample(example);
        if (count > 0) {
            //如果已存在
            return new Result(false, "品牌名称已存在");
        }
        brandMapper.insert(brand);
        return new Result(true, "添加成功");

    }

    //根据id查找品牌
    @Override
    public TbBrand findById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    //根据id更新品牌信息
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    //根据id数组删除品牌信息
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    //下拉列表
    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
