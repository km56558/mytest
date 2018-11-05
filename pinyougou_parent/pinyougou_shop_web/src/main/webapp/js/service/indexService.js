app.service("indexService",function ($http) {
    this.showLoginName = function () {
        return $http.get("../login/name.do");
    }
})