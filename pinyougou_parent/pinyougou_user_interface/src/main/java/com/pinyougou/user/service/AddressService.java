package com.pinyougou.user.service;

import java.util.List;

import com.pinyougou.pojo.TbAddress;

import entity.Result;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface AddressService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbAddress> findAll();


    /**
     * 增加
     */
    public Result add(TbAddress address);


    /**
     * 修改
     */
    public void update(TbAddress address);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbAddress findById(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long ids);

    //	根据用户查询地址
    List<TbAddress> findListByUserId(String userId);
}
