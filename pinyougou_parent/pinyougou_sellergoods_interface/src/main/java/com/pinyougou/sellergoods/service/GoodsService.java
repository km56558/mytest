package com.pinyougou.sellergoods.service;
import java.util.List;
import com.pinyougou.pojo.TbGoods;

import com.pinyougou.pojo.TbItem;
import com.pinyougou.vo.GoodsVo;
import entity.PageResult;
import entity.Result;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();

	
	
	/**
	 * 增加
     * @param goods
     */
	public Result add(GoodsVo goods);
	
	
	/**
	 * 修改
	 * @param goods
	 */
	public void update(GoodsVo goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public GoodsVo findById(Long id);
	
	
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
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

    void updateStatus(Long[] ids, String status);
	List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status);
}
