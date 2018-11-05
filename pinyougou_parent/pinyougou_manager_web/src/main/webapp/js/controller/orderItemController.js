 //控制层 
app.controller('orderItemController' ,function($scope,$controller   ,orderItemService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderItemService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	
	//修改回显
	$scope.findById=function(id){
		orderItemService.findById(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var obj;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			obj=orderItemService.update( $scope.entity); //修改
		}else{
			obj=orderItemService.add( $scope.entity);//增加
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
            orderItemService.del($scope.selectIds).success(
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
		orderItemService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}

});	
