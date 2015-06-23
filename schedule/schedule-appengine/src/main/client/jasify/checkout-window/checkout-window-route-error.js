(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowRouteErrorController', CheckoutWindowRouteErrorController);

    function CheckoutWindowRouteErrorController($log) {
        var vm = this;
        $log.error('Route error :-(');
    }
})(angular);