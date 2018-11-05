//服务层
app.service('typeTemplateService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../typeTemplate/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../typeTemplate/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../typeTemplate/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../typeTemplate/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../typeTemplate/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../typeTemplate/delete.do?ids=" + ids);
        }

        this.findTypeList = function () {
            return $http.get("../typeTemplate/findTypeList.do");
        }
        this.findSpecList = function (id) {
            return $http.get("../typeTemplate/findSpecList.do?id="+id);
        }
});
