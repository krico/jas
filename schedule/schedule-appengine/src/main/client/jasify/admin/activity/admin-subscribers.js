(function (angular) {

    angular.module('jasify.admin').controller('AdminSubscribersController', AdminSubscribersController);

    function AdminSubscribersController($location, subscriptions, Activity) {
        var vm = this;

        vm.subscriptions = subscriptions.items;
        vm.alerts = [];
        vm.alert = alert;
        vm.cancel = cancel;
        vm.back = back;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function cancel(id) {
            Activity.cancelSubscription(id).then(ok, fail);
            function ok(r) {
                vm.alert('warning', 'Subscription removed!');
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
            vm.alert('danger', 'Failed: ' + r.statusText);
        }

        function back() {
            $location.path("/admin/activities");
        }
    }

})(angular);