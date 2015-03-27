(function (angular) {

    angular.module('jasifyWeb').controller('CheckoutController', CheckoutController);

    function CheckoutController($log, cart) {
        var vm = this;
        vm.isEmpty = isEmpty;
        vm.alert = alert;
        vm.alerts = [];

        vm.cart = cart;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function isEmpty() {
            return !(vm.cart && vm.cart.items && vm.cart.items.length > 0);
        }
    }
})(angular);