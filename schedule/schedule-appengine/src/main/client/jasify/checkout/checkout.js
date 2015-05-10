(function (angular) {

    angular.module('jasify.checkout').controller('CheckoutController', CheckoutController);

    function CheckoutController($log, $rootScope, $window, $modal, ShoppingCart, Balance, cart) {
        var vm = this;
        vm.isEmpty = isEmpty;
        vm.alert = alert;
        vm.removeItem = removeItem;
        vm.alerts = [];
        vm.createPayment = createPayment;
        vm.cart = cart;
        vm.inProgress = false;
        vm.redirecting = false;
        vm.paymentType = 'PayPal';
        vm.paymentTypes = [
            {label: 'PayPal', id: 'PayPal'}//,
        //    {label: 'Pay at the door (cash)', id: 'Cash'}
        ];

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function isEmpty() {
            return !(vm.cart && vm.cart.items && vm.cart.items.length > 0);
        }

        function createPayment() {
            vm.inProgress = true;
            Balance.createCheckoutPayment({
                cartId: cart.id,
                type: vm.paymentType
            }).then(ok, fail);

            function ok(resp) {
                vm.redirecting = true;
                $log.debug("Redirecting: " + resp.approveUrl);
                $window.location.href = resp.approveUrl;
            }

            function fail(res) {
                vm.inProgress = false;
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
    }
})(angular);