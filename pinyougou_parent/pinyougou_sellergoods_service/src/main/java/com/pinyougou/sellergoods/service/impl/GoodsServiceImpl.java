package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}
	/**
	 * 增加
     * @param goodsVo
     */
	@Override
	public Result add(GoodsVo goodsVo) {
		TbGoods goods = goodsVo.getGoods();
		goods.setAuditStatus("0");
		goodsMapper.insert(goods);
		TbGoodsDesc goodsDesc = goodsVo.getGoodsDesc();
		goodsDesc.setGoodsId(goods.getId());
		goodsDescMapper.insert(goodsDesc);
		List<TbItem> itemList = goodsVo.getItemList();
		saveItemList(goodsVo);


		return new Result(true, "添加成功");
	}
	private void saveItemList(GoodsVo goods){
		if("1".equals(goods.getGoods().getIsEnableSpec())){
			for (TbItem item : goods.getItemList()) {
				String title = goods.getGoods().getGoodsName();
				Map<String,Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title+=" "+ specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(item,goods.getGoods());
				itemMapper.insert(item);
			}
		}else {
			TbItem item=new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());//商品KPU+规格描述串作为SKU名称
			item.setPrice( goods.getGoods().getPrice() );//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(item,goods.getGoods());
			itemMapper.insert(item);
		}
	}
	private void setItemValus(TbItem item,TbGoods goods){
		item.setGoodsId(goods.getId());//商品SPU编号
		item.setSellerId(goods.getSellerId());//商家编号
		item.setCategoryid(goods.getCategory3Id());//商品分类编号（3级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
		if (brand.getName()!=null) {
			item.setBrand(brand.getName());
		}
		//分类名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());
		if (itemCat.getName()!=null) {
			item.setCategory(itemCat.getName());
		}
		//商家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());
		if (seller.getNickName()!=null) {
			item.setSeller(seller.getNickName());
		}
	}

	
	/**
	 * 修改
	 * @param goodsVo
	 */
	@Override
	public void update(GoodsVo goodsVo){
		//设置未申请状态:如果是经过修改的商品，需要重新设置状态
		goodsVo.getGoods().setAuditStatus("0");

		goodsMapper.updateByPrimaryKey(goodsVo.getGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goodsVo.getGoodsDesc());//保存商品扩展表
		//删除原有的sku列表数据
		TbItemExample example=new TbItemExample();
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goodsVo.getGoods().getId());
		itemMapper.deleteByExample(example);
		//添加新的sku列表数据
		saveItemList(goodsVo);//插入商品SKU列表数据
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public GoodsVo findById(Long id){
		GoodsVo goodsVo = new GoodsVo();
		TbGoods goods = goodsMapper.selectByPrimaryKey(id);
		goodsVo.setGoods(goods);
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goodsVo.setGoodsDesc(goodsDesc);

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goodsVo.setItemList(itemList);
		return goodsVo; //goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
			criteria.andIsDeleteIsNull();//非删除状态
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
	}

}
