//服务层
app.service('seckillOrderService',function($http){
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../seckillOrder/findAll.do');		
	}
	//分页||条件查询
        this.search = function (page, size, entity) {
            return $http.post("../seckillOrder/search.do?page=" + page + "&size=" + size, entity);
        }
        //增加
        this.add = function (entity) {
            return $http.post("../seckillOrder/add.do", entity);
        }
        //修改
        this.update = function (entity) {
            return $http.post("../seckillOrder/update.do", entity);
        }
        //查询实体
        this.findById = function (id) {
            return $http.get("../seckillOrder/findById.do?id=" + id);
        }
        //删除
        this.del = function (ids) {
            return $http.get("../seckillOrder/delete.do?ids=" + ids);
        }
});
