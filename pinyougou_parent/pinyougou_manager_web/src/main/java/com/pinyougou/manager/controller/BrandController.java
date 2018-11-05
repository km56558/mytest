package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/brand")
@ResponseBody
public class BrandController {
    @Reference
    private BrandService brandService;

    //品牌列表
    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
       return brandService.findAll();
    }

    //品牌类别分页
    @RequestMapping("/search.do")
    public PageResult search(@RequestBody TbBrand brand , Integer page,Integer size){
        return brandService.findPage(brand,page,size);
    }

    //品牌添加
    @RequestMapping("/add.do")
    public Result add(@RequestBody TbBrand brand){
        try {
        //如果成功添加
            return brandService.add(brand);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //修改回显
    @RequestMapping("/findById.do")
    public TbBrand findById(Long id){
        return brandService.findById(id);
    }

    //修改
    @RequestMapping("/update.do")
    public Result update(@RequestBody TbBrand brand){
        try {
            //如果成功添加
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //删除
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            //如果成功添加
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //下拉列表
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
}
