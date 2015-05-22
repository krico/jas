(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowCloseController', CheckoutWindowCloseController);

    function CheckoutWindowCloseController($window, $timeout, $location, $cookies, BrowserData) {
        var vm = this;
        vm.loaded = false;
        vm.operationFailed = false;
        vm.onLoad = onLoad;
        vm.close = close;

        $timeout(function () {
            vm.onLoad();
        }, 1000);


        function onLoad() {
            vm.loaded = true;
            var ps = $location.search().paymentStatus;
            vm.operationFailed = !(ps && ps == 'success');
            if (vm.operationFailed) {
                $cookies.popupPaymentStatus = 'failed';
            } else {
                $cookies.popupPaymentStatus = 'success';
            }
            $timeout(function () {
                vm.close();
            }, 2000);
        }

        function close() {
            $window.close();
        }
    }
})(angular);