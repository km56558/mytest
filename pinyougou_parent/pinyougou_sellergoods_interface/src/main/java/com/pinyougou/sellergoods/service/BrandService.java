package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    List<TbBrand> findAll();

    PageResult findPage(TbBrand brand, Integer page, Integer size);

    Result add(TbBrand brand);

    TbBrand findById(Long id);

    void update(TbBrand brand);

    void delete(Long[] ids);

    List<Map> selectOptionList();
}
