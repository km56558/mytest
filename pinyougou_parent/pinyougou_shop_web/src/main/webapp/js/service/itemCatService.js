//服务层
app.service('itemCatService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../itemCat/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../itemCat/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../itemCat/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../itemCat/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../itemCat/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../itemCat/delete.do?ids=" + ids);
        }
        //根据parentId加载页面
        this.findByParentId = function (parentId) {
            return $http.get("../itemCat/findByParentId.do?parentId="+parentId);
        }
});
