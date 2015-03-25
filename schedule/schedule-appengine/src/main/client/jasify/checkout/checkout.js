(function (angular) {

    angular.module('jasifyWeb').controller('CheckoutController', CheckoutController);

    function CheckoutController() {
        var vm = this;
        vm.alert = alert;
        vm.alerts = [];

        vm.cart = {
            currency: "CHF",
            total: 120,
            grandTotal: 125.23,
            items: [
                {description: "MetaFit ipsum lorem", units: 1, price: 20},
                {description: "Spint ipsum lorem", units: 2, price: 100}
            ]
        };

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }
    }
})(angular);