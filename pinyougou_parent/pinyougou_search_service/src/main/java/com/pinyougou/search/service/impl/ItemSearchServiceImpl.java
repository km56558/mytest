package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
//        Query query = new SimpleQuery("*:*");
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("rows",page.getContent());
//        return map;
        //高亮显示关键字
        Map<String, Object> map = new HashMap<>();
        map.putAll(searchList(searchMap));
        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

            String category = (String) searchMap.get("category");
            if (!"".equals(category)){
                map.putAll(searchBrandAndSpecList(category));
            }else {
                if(categoryList.size()>0) {
                    map.putAll(searchBrandAndSpecList(categoryList.get(0)));
                }
            }

        return map;
    }
    //设置高亮内容
    private Map<String,Object> searchList(Map searchMap){
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置过滤条件
        //按分类过滤
        String category = (String) searchMap.get("category");
        if (!"".equals(category) && category!=null) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //按品牌
        String brand = (String) searchMap.get("brand");
        if (!"".equals(brand) && brand!=null) {
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //按规格
        Map<String,String> spec = (Map<String, String>) searchMap.get("spec");
        if (spec.size()>0){
            for (String key : spec.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_spec_"+key).is(spec.get(key));
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }


        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for (HighlightEntry<TbItem> tbItemHighlightEntry : tbItems.getHighlighted()) {//循环高亮入口集合
            TbItem item = tbItemHighlightEntry.getEntity();//获得原实体类
            List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            if (highlights.size()>0) {
                for (HighlightEntry.Highlight highlight : highlights) {
                    List<String> snipplets = highlight.getSnipplets();
                    if (snipplets.size()>0) {
                        for (String snipplet : snipplets) {
                            item.setTitle(snipplet);//设置高亮的结果
                        }
                    }
                }
            }
        }
        Map<String,Object> map=new HashMap<>();
        map.put("rows",tbItems.getContent());
        return map;
    }

    //查询分类列表
    private  List<String> searchCategoryList(Map searchMap){
        List<String> list = new ArrayList<>();
        Query query = new SimpleQuery("*:*");
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> entriesContent = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entriesContent) {
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }

        return list;

    }
    //查询品牌和规格列表
    private Map<String,Object> searchBrandAndSpecList(String category){
        Map<String,Object> map = new HashMap<>();
        //获取模板ID值
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null){
            //获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            //放入map中
            map.put("brandList",brandList);
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;

    }

}
