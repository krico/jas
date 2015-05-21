(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowCloseController', CheckoutWindowCloseController);

    function CheckoutWindowCloseController($log, $rootScope, $window, $timeout, $location) {
        var vm = this;
        vm.operationFailed = false;
        vm.onLoad = onLoad;

        $timeout(function () {
            vm.onLoad();
        }, 1000);


        function onLoad() {
            var ps = $location.search().paymentStatus;
            if (ps && ps == 'success') {
                vm.operationFailed = false;
            } else {
                vm.operationFailed = true;
            }
            $timeout(function () {
                $window.close();
            }, 5000);
        }
    }
})(angular);