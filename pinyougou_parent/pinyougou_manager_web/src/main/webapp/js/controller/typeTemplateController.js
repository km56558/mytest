 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService ,specificationService,brandService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	
	//修改回显
	$scope.findById=function(id){
		typeTemplateService.findById(id).success(
			function(response){
				$scope.entity= response;
                $scope.entity.brandIds=  JSON.parse( $scope.entity.brandIds);
                $scope.entity.specIds= JSON.parse($scope.entity.specIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);

            }
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var obj;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			obj=typeTemplateService.update( $scope.entity); //修改
		}else{
			obj=typeTemplateService.add( $scope.entity);//增加
		}				
		obj.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
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
            typeTemplateService.del($scope.selectIds).success(
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
		typeTemplateService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

    $scope.brandList={data:[]};//品牌列表

    //读取品牌列表
    $scope.findBrandList=function(){
        brandService.selectOptionList().success(
            function(response){
                $scope.brandList={data:response};
            }
        );
    }

    $scope.specList={data:[]};//规格列表

    //读取规格列表
    $scope.findSpecList=function(){
        specificationService.selectOptionList().success(
            function(response){
                $scope.specList={data:response};
            }
        );
    }

    $scope.addTableRow=function(){
        $scope.entity.customAttributeItems.push({});
    }
    //删除规格选项行
    $scope.deleTableRow=function(index){
        $scope.entity.customAttributeItems.splice(index,1);
    }

});	
