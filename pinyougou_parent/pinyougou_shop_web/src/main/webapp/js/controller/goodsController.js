 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	
	//修改回显
	$scope.findById=function(){
        var id= $location.search()['id'];
        if(id==null){
            return ;
        }
		goodsService.findById(id).success(
			function(response){
				$scope.entity= response;
                editor.html($scope.entity.goodsDesc.introduction);
                //显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //规格
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //SKU列表规格列转换
                for( var i=0;i<$scope.entity.itemList.length;i++ ){
                    $scope.entity.itemList[i].spec =
                        JSON.parse( $scope.entity.itemList[i].spec);
                }
            }
		);				
	}
	
	//保存 
	$scope.save=function(){
	    var obj;
        if($scope.entity.goods.id!=null){//如果有ID
            obj=goodsService.update( $scope.entity ); //修改
        }else{
            obj=goodsService.add( $scope.entity  );//增加
        }
        $scope.entity.goodsDesc.introduction=editor.html();
            obj.success(
			function(response){
				if(response.success){
                    alert('保存成功');
                    $scope.entity={};
                    location.href="goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.del=function() {
        //获取选中的复选框
        if (confirm('确定要删除吗？')) {
            //遍历列表将checked为true的值放入ids数组
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].ck) {
                    $scope.selectIds.push($scope.list[i].id);
                }
            }
            goodsService.del($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                    } else {
                        alert(response.message);
                    }
                }
            )
            //将ids数组清空
            $scope.selectIds = [];
        }
    }
	
	$scope.searchEntity={};//定义搜索对象

	//搜索
	$scope.search=function(page,size){
		goodsService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

	// 上传
    $scope.uploadFile=function(){
        uploadService.uploadFile().success(function(response) {
            if(response.success){//如果上传成功，取出url
                $scope.image_entity.url=response.message;//设置文件地址
            }else{
                alert(response.message);
            }
        }).error(function() {
            alert("上传发生错误");
        });
    };
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    //删除图片
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(function (response) {

			$scope.itemCat1List = response;
        })
    };

    $scope.$watch("entity.goods.category1Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
        })
		
    });

    $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })

    });


    $scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {
        itemCatService.findById(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })

    });

    $scope.$watch("entity.goods.typeTemplateId",function (newValue, oldValue) {

        typeTemplateService.findById(newValue).success(function (response) {
            $scope.typeTemplate = response;
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
            if($location.search()['id']==null) {
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);//扩展属性
            }
            });
        //规格
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList=response;
        });

    });


    $scope.updateSpecAttribute = function ($event,name,value) {
        var obj= $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems ,'attributeName', name);
        if (obj != null) {
            //如果列表中已经有了该attributeName
            if($event.target.checked){
                //如果为勾选
                obj.attributeValue.push(value);
            }else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);//移除选项
                if (obj.attributeValue.length == 0){
                    //如果选项已经为空,则在$scope.entity.goodsDesc.specificationItems中删除掉 attributeName:attributeValue[]这项键值对
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
                }
            }
        }else {
            //如果列表中没有该attributeName
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]})
        }
    }

    $scope.createItemList=function() {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < items.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
    addColumn = function (list, name, value) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
           var oldRow = list[i];
            for (var j = 0; j < value.length; j++){
                var newRow= JSON.parse( JSON.stringify( oldRow ));//深拷贝
                newRow.spec[name] = value[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态
    $scope.itemCatList=[];//商品分类列表
//加载商品分类列表
    $scope.findItemCatList=function(){
        itemCatService.findAll().success(
            function(response){
                for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
    }

    //根据规格名和选项名返回是否被勾选
    $scope.checkAttributeValue=function(specName,optionName){
        var items= $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(items,'attributeName',specName);
        if (obj !=null){
            if(obj.attributeValue.indexOf(optionName)>=0){
                return true;
            }
        }
        return false;

    }

    });
