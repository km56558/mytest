//服务层
app.service('orderService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../order/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../order/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../order/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../order/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../order/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../order/delete.do?ids=" + ids);
        }
});
