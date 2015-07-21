(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribersController', AdminSubscribersController);

    function AdminSubscribersController($location, $filter, subscriptions, Activity) {
        var vm = this;

        vm.subscriptions = subscriptions.items;
        vm.cancel = cancel;
        vm.back = back;

        var $translate = $filter('translate');

        function cancel(id) {
            Activity.cancelSubscription(id).then(ok, fail);
            function ok(r) {
                var translation = $translate('SUBSCRIPTION_REMOVED');
                jasDialogs.success(activityUpdatedTranslation);
                var newS = [];
                angular.forEach(vm.subscriptions, function (value, key) {
                    if (id != value.id) {
                        this.push(value);
                    }
                }, newS);
                vm.subscriptions = newS;
            }
        }

        function fail(r) {
            var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
            jasDialogs.resultError(failedPleaseRetryTranslation, r);
        }

        function back() {
            $location.path("/admin/activities");
        }
    }

})(angular);