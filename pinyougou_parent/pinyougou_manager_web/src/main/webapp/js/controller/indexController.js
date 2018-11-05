app.controller('indexController', function ($scope, loginService, $controller) {
    $scope.showLoginName = function () {
        loginService.loginName().success(function (response) {
            $scope.loginName = response;
        });
    }
});