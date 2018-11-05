app.controller("indexController",function ($scope,$controller, indexService) {

    $scope.showLoginName = function () {
        indexService.showLoginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }
})