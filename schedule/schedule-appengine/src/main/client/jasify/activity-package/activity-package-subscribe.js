(function (angular) {

    angular.module('jasify.activityPackage').controller('ActivityPackageSubscribeController', ActivityPackageSubscribeController);

    function ActivityPackageSubscribeController($log, $location, ShoppingCart, activityPackage, activityPackageActivities) {
        var vm = this;
        vm.activityPackage = activityPackage;
        vm.activityPackageActivities = activityPackageActivities;
        vm.selectedActivities = {};
        vm.selectedActivitiesCount = selectedActivitiesCount;
        vm.remainingActivitiesToSelect = remainingActivitiesToSelect;
        vm.bookIt = bookIt;


        function selectedActivitiesCount() {
            var count = 0;
            angular.forEach(vm.selectedActivities, function (selected, key) {
                if (selected)
                    ++count;
            });
            return count;
        }

        function remainingActivitiesToSelect() {
            var count = vm.selectedActivitiesCount();
            return vm.activityPackage.itemCount - count;
        }

        function bookIt() {
            //TODO: WARN/Confirm if user is booking less then itemCount
            var activities = [];
            angular.forEach(vm.selectedActivities, function (selected, key) {
                if (selected) activities.push(key);
            }, activities);

            return ShoppingCart.addUserActivityPackage(vm.activityPackage, activities).then(ok, nok);

            function ok(r) {
                $location.path('/checkout');
            }

            function nok(r) {
                $log.warn('failed: ' + angular.toJson(r));
            }
        }
    }
})(angular);