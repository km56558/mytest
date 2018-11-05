app.controller("brandController", function ($scope, brandService,$controller) {
    $controller("baseController",{$scope:$scope});

    //查询列表
    $scope.findAll = function () {
        brandService.findAll().success(
            function (data) {
                $scope.list = data;
            }
        );
    };

    $scope.searchEntity = {};
    //分页
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(
            function (data) {
                $scope.list = data.rows;
                $scope.paginationConf.totalItems = data.total;
            }
        );
    };

    //保存(添加和修改)
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity)
        }
        obj.success(
            function (data) {
                if (data.success) {
                    $scope.reloadList();
                } else {
                    alert(data.message);
                }
            }
        );
    };
    //修改回显
    $scope.findById = function (id) {
        brandService.findById(id).success(
            function (data) {
                $scope.entity = data;
            }
        )
    };
    //删除
    $scope.del = function () {
        if (confirm('确定要删除吗？')) {
            //遍历列表将checked为true的值放入ids数组
            for (var i = 0; i < $scope.list.length; i++) {
                if ($scope.list[i].ck) {
                    $scope.selectIds.push($scope.list[i].id);
                }
            }
            brandService.del($scope.selectIds).success(
                function (data) {
                    if (data.success) {
                        $scope.reloadList();
                    } else {
                        alert(data.message);
                    }
                }
            )
            //将ids数组清空
            $scope.selectIds = [];
        }

    }
});