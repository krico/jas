(function (angular) {

    angular.module('jasify.authenticate').controller('OAuth2Controller', OAuth2Controller);

    function OAuth2Controller($routeParams, $location, $scope, Auth) {
        var vm = this;
        vm.authenticate = authenticate;
        vm.status = 'Preparing authentication ...';
        vm.alerts = [];
        vm.alert = alert;
        vm.callbackUrl = $routeParams.callbackUrl;
        vm.complete = false;

        vm.authenticate(vm.callbackUrl);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function authenticate(url) {
            vm.status = 'Authenticating ...';
            Auth.providerAuthenticate(url).then(authenticated, failed);

            function authenticated(resp) {
                vm.status = 'Loading user credentials ...';
                var path = resp.result.data || "/";
                if (path.indexOf("/") !== 0) {
                    path = "/" + path;
                }

                if (path.indexOf('/logout') === 0) {
                    path = "/";
                }

                Auth.restore(true).then(ok, fail);

                function ok(u) {
                    if ($scope.setCurrentUser) $scope.setCurrentUser(u);
                    $location.replace();
                    $location.path(path);
                }

                function fail(msg) {
                    vm.complete = true;
                    vm.alert('danger', 'Failed to load credentials');
                    //TODO: handle failure
                    $location.replace();
                    $location.path("/");
                }

            }

            function failed(reason) {
                vm.complete = true;
                vm.alert('danger', 'Failed to authenticate');
                //TODO: handle failure
                $location.replace();
                $location.path("/");
            }
        }
    }
})(angular);