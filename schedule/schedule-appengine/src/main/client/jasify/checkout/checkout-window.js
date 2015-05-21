(function (angular) {

    angular.module('jasify.checkoutWindow').controller('CheckoutWindowController', CheckoutWindowController);

    function CheckoutWindowController($log, $window, $timeout, Balance, BrowserData, cart) {
        var vm = this;
        vm.message = 'Initializing ...';
        vm.operationFailed = false;
        vm.onLoad = onLoad;

        $timeout(function () {
            vm.onLoad();
        }, 1000);


        function onLoad() {
            vm.operationFailed = false;
            vm.message = 'Creating payment ...';
            Balance.createCheckoutPayment({
                cartId: cart.id,
                type: 'PayPal'
            }).then(ok, fail);

            function ok(resp) {
                vm.operationFailed = false;
                vm.message = 'Redirecting to payment provider (this might take a few seconds) ...';
                $log.debug("Redirecting: " + resp.approveUrl);
                BrowserData.setPaymentAcceptRedirect('/close');
                BrowserData.setPaymentCancelRedirect('/close');
                BrowserData.setPaymentCancelRedirectAuto(true);
                $window.location.href = resp.approveUrl;
            }

            function fail(res) {
                vm.operationFailed = true;
                $log.error('Operation failed: ' + angular.toJson(res));
                vm.message = 'Operation failed! (' + res.statusText + ')';
            }
        }
    }
})(angular);