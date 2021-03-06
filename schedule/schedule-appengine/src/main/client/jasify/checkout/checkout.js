(function (angular) {

    angular.module('jasify.checkout').controller('CheckoutController', CheckoutController);

    function CheckoutController($log, $rootScope, $scope, $window, $modal, $location, $cookies, ShoppingCart, Balance, PopupWindow,
                                Checkout, BrowserData, cart) {
        var vm = this;
        vm.isEmpty = isEmpty;
        vm.alert = alert;
        vm.removeItem = removeItem;
        vm.alerts = [];
        vm.createPayment = Checkout.popupMode ? createPaymentPopup : createPayment;
        vm.cart = cart;
        vm.cancel = BrowserData.getPaymentCancelRedirect();
        vm.inProgress = false;
        vm.paymentTypes = [];
        vm.paymentType = undefined;
        vm.paymentButtonText = "";
        vm.init = init;
        vm.updateCheckoutButtonText = updateCheckoutButtonText;

        vm.init();

        function init() {
            updateCheckoutButtonText();
            for (i = 0; i < vm.cart.paymentOptions.length; i++) {
                var paymentOption = vm.cart.paymentOptions[i];
                if (paymentOption.paymentType == 'PayPal') {
                    var payPalInfo = {label: 'PayPal', id: 'PayPal', button: 'Continue to PayPal',
                        enabled: paymentOption.enabled, disabledReason: paymentOption.disabledReason,
                        fee: paymentOption.fee, feeReason: paymentOption.feeReason};
                    vm.paymentTypes.push(payPalInfo);
                } else if (paymentOption.paymentType == 'Invoice') {
                    var invoiceInfo = {label: 'Electronic Invoice', id: 'Invoice', button: 'Generate Electronic Invoice',
                        enabled: paymentOption.enabled, disabledReason: paymentOption.disabledReason,
                        fee: paymentOption.fee, feeReason: paymentOption.feeReason};
                    vm.paymentTypes.push(invoiceInfo);
                //} else if (paymentOption.paymentType == 'Cash') {
                //    var cashInfo = {label: 'Pay at the door (cash)', id: 'Cash', button: 'Continue and pay later',
                //        enabled: paymentOption.enabled, disabledReason: paymentOption.disabledReason,
                //        fee: paymentOption.fee, feeReason: paymentOption.feeReason};
                //    vm.paymentTypes.push(cashInfo);
                }
           }
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function isEmpty() {
            return !(vm.cart && vm.cart.items && vm.cart.items.length > 0);
        }

        function createPaymentPopup() {
            if (vm.inProgress) {
                return;
            }
            vm.inProgress = true;
            updateCheckoutButtonText();
            var acceptRedirect = BrowserData.getPaymentAcceptRedirect();
            var cancelRedirect = BrowserData.getPaymentCancelRedirect();
            delete $cookies.popupPaymentStatus;
            PopupWindow.open('/checkout-window.html', {width: 820}).then(ok, fail);
            function ok() {
                vm.inProgress = false;
                if ($cookies.popupPaymentStatus == 'success') {
                    $location.search({paymentStatus: 'success'});
                    $location.path(acceptRedirect);
                } else {
                    $location.search({paymentStatus: 'failed'});
                    $location.path(cancelRedirect);
                }
            }

            function fail(res) {
                vm.inProgress = false;
                updateCheckoutButtonText();
                alert('danger', 'Failed: ' + res);
            }
        }

        function updateCheckoutButtonText() {
            if (vm.inProgress) {
                vm.paymentButtonText = "Working...";
            } else if (!vm.paymentType) {
                vm.paymentButtonText = "Select a Payment Method";
            } else {
                vm.paymentButtonText = vm.paymentType.button;
            }
        }

        function createPayment() {
            if (vm.inProgress) {
                return;
            }
            vm.inProgress = true;
            updateCheckoutButtonText();
            Balance.createCheckoutPayment({
                cartId: cart.id,
                type: vm.paymentType.id
            }).then(ok, fail);

            function ok(resp) {
                $log.debug("Redirecting: " + resp.approveUrl);
                $window.location.href = resp.approveUrl;
            }

            function fail(res) {
                vm.inProgress = false;
                updateCheckoutButtonText();
                alert('danger', 'Failed: ' + res.statusText);
            }
        }

        function removeItem(item) {
            var scope = $rootScope.$new();
            scope.item = item;
            var modalInstance = $modal.open({
                //TODO: should bring up login.html some how
                templateUrl: 'checkout/checkout-remove-item-modal.html',
                size: 'sm',
                scope: scope
            });

            modalInstance.result.then(yes, no);

            function yes(item) {
                ShoppingCart.removeItem(vm.cart, item).then(ok, fail);
                function ok(resp) {
                    vm.cart = resp;
                }

                function fail() {
                    alert('danger', 'Failed to remove item');
                }
            }

            function no() {
                $log.info('Modal dismissed at: ' + new Date());
            }
        }

        $scope.$watch('vm.paymentType.id', function (newVal, oldVal) {
            updateCheckoutButtonText();
        });
    }
})(angular);