(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribersController', AdminSubscribersController);

    function AdminSubscribersController($location, $filter, subscriptions, Activity) {
        var vm = this;

        vm.subscriptions = subscriptions.items;
        vm.alerts = [];
        vm.alert = alert;
        vm.cancel = cancel;
        vm.back = back;

        var $translate = $filter('translate');

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function cancel(id) {
            Activity.cancelSubscription(id).then(ok, fail);
            function ok(r) {
                var translation = $translate('SUBSCRIPTION_REMOVED');
                vm.alert('warning', translation);
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
            var translation = $translate('FAILED');
            vm.alert('danger', translation + ': ' + r.statusText);
        }

        function back() {
            $location.path("/admin/activities");
        }
    }

})(angular);