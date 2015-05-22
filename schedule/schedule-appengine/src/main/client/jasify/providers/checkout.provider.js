(function (angular) {

    /**
     * Ok this is a waste of provider, but to allow a module to configure
     * whether the checkout should be executed in a popup or not, I created it.
     *
     * function (CheckoutProvider) {
     *       CheckoutProvider.popupMode(true);
     *  })
     */
    angular.module('jasifyComponents').provider('Checkout', CheckoutProvider);

    function CheckoutProvider() {
        var popupMode = false;

        this.popupMode = function (v) {
            if (v) popupMode = v;
            return popupMode;
        };

        this.$get = checkout;

        function checkout() {
            return {popupMode: popupMode};
        }
    }

})(angular);