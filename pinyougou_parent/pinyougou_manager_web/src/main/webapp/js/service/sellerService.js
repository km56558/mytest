//服务层
app.service('sellerService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seller/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../seller/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../seller/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../seller/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../seller/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../seller/delete.do?ids=" + ids);
        }
        this.updateStatus = function (sellerId,status) {
	    return $http.get("../seller/updateStatus.do?sellerId="+sellerId+"&status="+status);
            
        }
});
