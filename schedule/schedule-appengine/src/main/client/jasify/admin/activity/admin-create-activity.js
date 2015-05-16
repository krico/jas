(function (angular) {

    angular.module('jasify.admin').controller('AdminCreateActivityController', AdminCreateActivityController);

    function AdminCreateActivityController($location, $moment, Activity, aButtonController, ActivityType, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.loadActivityTypes = loadActivityTypes;
        vm.activityTypeChanged = activityTypeChanged;
        vm.activity = {
            fromDate: $moment().format('DD/MM/YYYY'),
            fromTime: $moment().add(1, 'hour').format('LT'),
            toDate: $moment().format('DD/MM/YYYY'),
            toTime: $moment().add(2, 'hour').format('LT')
        };

        vm.create = create;

        vm.repeatDetails = {
            repeatType: "No",
            repeatUntilType: "Date"
        }

        function loadActivityTypes(organization) {

            vm.activityTypes = [];

            if (!organization.id) return;

            ActivityType.query(organization).then(ok, fail);

            function ok(r) {
                vm.activityTypes = r.items;
                selectActivityType(vm.activityTypes, vm.activity);
            }

            function fail(r) {
                //vm.alert('danger', 'Failed to load activity types');
            }
        }

        function create() {

            vm.activity.start = moment(vm.activity.fromDate + " " + vm.activity.fromTime, "DD/MM/YYYY LT");
            vm.activity.finish = moment(vm.activity.toDate + " " + vm.activity.toTime, "DD/MM/YYYY LT");

            Activity.add(vm.activity, vm.repeatDetails).then(ok, fail);

            function ok(r) {
                if (r.items.length == 1) {
                    $location.path('/admin/activity/' + r.items[0].id);
                } else {
                    $location.path("/admin/activities");
                }
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }

        function selectActivityType(activityTypes, activity) {
            if (vm.activityTypes.length == 1) {
                vm.activity.activityType = vm.activityTypes[0];
                vm.activityTypeChanged();
            } else if  (activity.activityType && activity.activityType.id) {
                angular.forEach(activityTypes, function (value, key) {
                    if (activity.activityType.id == value.id) {
                        vm.activity.activityType = value;
                    }
                });
            }
        }

        function activityTypeChanged() {
            vm.activity.description = vm.activity.activityType.description;
            vm.activity.price = vm.activity.activityType.price;
            vm.activity.currency = vm.activity.activityType.currency;
            vm.activity.location = vm.activity.activityType.location;
            vm.activity.maxSubscriptions = vm.activity.activityType.maxSubscriptions;
        }
    }

})(window.angular);