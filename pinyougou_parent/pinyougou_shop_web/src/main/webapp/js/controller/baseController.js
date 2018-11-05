app.controller("baseController",function ($scope) {
    //分页控件配置currentPage:当前页   totalItems :总记录数
    // itemsPerPage:每页记录数  perPageOptions :分页选项  onChange:当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();
        }
    };
    //刷新列表
    $scope.reloadList = function () {
        $scope.All = false;
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    $scope.selectIds = [];//用户勾选的ID集合
    //全选
    $scope.checkAll = function ($event) {
        //遍历列表设置checked值
        for (var i = 0; i < $scope.list.length; i++) {
            $scope.list[i].ck = $event.target.checked;
        }
    }

    $scope.jsonToString=function(jsonString,key){

        var json= JSON.parse(jsonString);
        var value="";

        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=", ";
            }
            value +=json[i][key];
        }

        return value;
    }
//在list集合中根据某key的值查询对象
    $scope.searchObjectByKey = function(list,key,value){
        for (var i = 0; i <list.length ; i++){
            if (list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }
});