(function (angular) {

    angular.module('jasify.bookIt').controller('BookItSubscribeController', BookItSubscribeController);

    function BookItSubscribeController($location, $log, Session, Activity, ShoppingCart, BrowserData, activity) {
        var vm = this;
        vm.activity = activity;
        vm.bookIt = bookIt;
        vm.checkSubscribed = checkSubscribed;
        vm.inProgress = false;
        vm.subscription = null;
        vm.showBookIt = showBookIt;
        vm.showFullyBooked = showFullyBooked;

        vm.checkSubscribed();

        function showBookIt() {
            if (vm.subscription === null) {
                return true;
            }
            return !vm.subscription.id;

        }

        function showFullyBooked() {
            if (vm.showBookIt()) {
                return (activity.maxSubscriptions - activity.subscriptionCount) <= 0;
            } else {
                return false;
            }
        }

        function checkSubscribed() {
            $log.debug(angular.toJson(activity));
            if (vm.activity.id) {
                vm.inProgress = true;
                Activity.isSubscribed(Session.userId, vm.activity).then(subscribed, notSubscribed);
            }

            function subscribed(subscription) {
                vm.inProgress = false;
                vm.subscription = subscription;
            }

            function notSubscribed() {
                vm.inProgress = false;
            }
        }

        function bookIt() {
            vm.inProgress = true;
            ShoppingCart.addUserActivity(vm.activity).then(added, failed);
            function added(cart) {
                vm.inProgress = false;
                BrowserData.setPaymentAcceptRedirect($location.path());
                $location.path('/checkout');
            }

            function failed() {
                vm.inProgress = false;
                //TODO: handle error
            }
        }
    }
})(angular);