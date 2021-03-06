(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowCloseController', CheckoutWindowCloseController);

    function CheckoutWindowCloseController($window, $timeout, $location, $cookies, CHECKOUT_WINDOW) {
        var vm = this;
        vm.loaded = false;
        vm.operationFailed = false;
        vm.onLoad = onLoad;
        vm.close = close;

        $timeout(function () {
            vm.onLoad();
        }, 500);


        function onLoad() {
            vm.loaded = true;
            var ps = $location.search().paymentStatus;
            vm.operationFailed = !(ps && ps == 'success');
            if (vm.operationFailed) {
                $cookies[CHECKOUT_WINDOW.statusCookie] = CHECKOUT_WINDOW.statusPaymentFailed;
            } else {
                $cookies[CHECKOUT_WINDOW.statusCookie] = CHECKOUT_WINDOW.statusSuccess;
            }
            $timeout(function () {
                vm.close();
            }, 2500);
        }

        function close() {
            $window.close();
        }
    }
})(angular);