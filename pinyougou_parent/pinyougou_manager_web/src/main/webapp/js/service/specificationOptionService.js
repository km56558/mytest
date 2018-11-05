//服务层
app.service('specificationOptionService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../specificationOption/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../specificationOption/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../specificationOption/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../specificationOption/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../specificationOption/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../specificationOption/delete.do?ids=" + ids);
        }
});
