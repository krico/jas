(function (angular) {

    angular.module('jasify.bookIt').controller('BookItSubscribeController', BookItSubscribeController);

    function BookItSubscribeController(Session, Activity, activity) {
        var vm = this;
        vm.activity = activity;
        vm.bookIt = bookIt;
        vm.checkSubscribed = checkSubscribed;
        vm.inProgress = false;
        vm.subscription = null;
        vm.showBookIt = showBookIt;

        vm.checkSubscribed();

        function showBookIt() {
            if (vm.subscription === null) {
                return true;
            }
            return !vm.subscription.id;

        }

        function checkSubscribed() {
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
            Activity.subscribe(Session.userId, vm.activity).then(subscribed, failed);

            function subscribed(subscription) {
                vm.inProgress = false;
                vm.subscription = subscription;
            }

            function failed() {
                vm.inProgress = false;
                //TODO: handle error
            }
        }
    }
})(angular);