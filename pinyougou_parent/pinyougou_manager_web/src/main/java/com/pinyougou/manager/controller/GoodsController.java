package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;

import entity.PageResult;
import entity.Result;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout=4000000)
	private GoodsService goodsService;
//	@Reference(timeout=4000000)
//	private ItemSearchService itemSearchService;
//	@Reference(timeout=4000000)
//	private ItemPageService itemPageService;
	@Resource
	private Destination queueSolrImportDestination;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource
	private Destination queueSolrDeleteDestination;
	@Resource
	private Destination topicPageImportDestination;
	@Resource
	private Destination topicPageDeleteDestination;//用于删除静态网页的消息
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();

	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult  search(@RequestBody TbGoods goods,int page,int size){
		return goodsService.findPage(goods,page,size);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody GoodsVo goods){
		try {
		//如果成功添加
		return goodsService.add(goods);
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"添加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody GoodsVo goods){
		try {
		//如果成功添加
		goodsService.update(goods);
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
	public GoodsVo findById(Long id){
		return goodsService.findById(id);
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
		goodsService.delete(ids);
//		itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);

				}
			});

			//删除页面
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true,"删除成功");
		}catch (Exception e){
		e.printStackTrace();
		return new Result(false,"删除失败");
		}
	}

	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){
		try {
			goodsService.updateStatus(ids, status);
			if ("1".equals(status)){
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
				if(itemList.size()>0){
//					itemSearchService.importList(itemList);
					String jsonString = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrImportDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {

							return session.createTextMessage(jsonString);
						}
					});

				}else{
					System.out.println("没有明细数据");
				}
				//静态页生成
				for(Long goodsId:ids){
					genHtml(goodsId);
				}
			}
			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		jmsTemplate.send(topicPageImportDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(goodsId+"");
			}
		});
	}
	
}
