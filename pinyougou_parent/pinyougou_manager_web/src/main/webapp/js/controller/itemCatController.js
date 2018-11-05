 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	
	//修改回显
	$scope.findById=function(id){
		itemCatService.findById(id).success(
			function(response){
				$scope.entity= response;
				$scope.entity.typeId = response.typeId;
			}
		);				
	}

	
	//保存 
	$scope.save=function(){				
		var obj;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			obj=itemCatService.update( $scope.entity); //修改
		}else{
            $scope.entity.parentId  = $scope.parentId;
			obj=itemCatService.add( $scope.entity);//增加
		}				
		obj.success(
			function(response){
				if(response.success){
					//重新查询 
                    $scope.findByParentId($scope.parentId);//重新加载
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
            itemCatService.del($scope.selectIds).success(
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
		itemCatService.search(page,size,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}
		);
	}
    $scope.parentId = 0;
	$scope.findByParentId = function (parentId) {
		$scope.parentId = parentId;
		itemCatService.findByParentId(parentId).success(
			function (response) {
				$scope.list=response;
            }
		)
    }

    $scope.grade = 1;//分类级别默认为1
    $scope.setGrade=function(value){
        $scope.grade=value;
    };
    //读取列表
    $scope.selectList=function(p_entity) {

        if ($scope.grade == 1) {
            $scope.entity_2 = null;
            $scope.entity_3 = null;
        }
            if ($scope.grade == 2) {
                $scope.entity_2 = p_entity;
                $scope.entity_3 = null;
            }
            if ($scope.grade == 3) {

                $scope.entity_3 = p_entity;
            }

            $scope.findByParentId(p_entity.id)
        }

        $scope.findTypeList = function () {
            typeTemplateService.findTypeList().success(function (response) {
				$scope.typeList = response;
            })
        }

    });
