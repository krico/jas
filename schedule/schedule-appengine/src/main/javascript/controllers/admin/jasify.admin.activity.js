(function (angular) {

    angular.module('jasifyScheduleControllers').controller('AdminActivityController', AdminActivityController);

    function AdminActivityController($log, dateFilter, ActivityType, Activity, activity, organizations) {
        var vm = this;

        vm.dateTimeFormat = 'dd MMM yyyy HH:mm';
        vm.alerts = [];
        vm.organization = {};
        vm.activity = activity;
        vm.activityTypes = [];
        vm.loadingActivityTypes = false;

        vm.isStartOpen = false;
        vm.openStart = openStart;

        vm.isFinishOpen = false;
        vm.openFinish = openFinish;

        vm.organizations = organizations.items;
        vm.selectOrganization = selectOrganization;
        vm.selectActivityType = selectActivityType;
        vm.loadActivityTypes = loadActivityTypes;
        vm.hasActivityTypes = hasActivityTypes;
        vm.alert = alert;
        vm.save = save;
        vm.reset = reset;

        vm.selectOrganization(vm.organizations, vm.activity);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function openStart($event) {
            $event.preventDefault();
            $event.stopPropagation();

            vm.isStartOpen = true;
        }

        function openFinish($event) {
            $event.preventDefault();
            $event.stopPropagation();

            vm.isFinishOpen = true;
        }

        function hasActivityTypes() {
            if (vm.loadingActivityTypes) return true;
            return vm.activityTypes.length !== 0;
        }

        function loadActivityTypes(organization) {
            vm.loadingActivityTypes = true;

            vm.activityTypes = [];

            if (!organization.id) return;

            ActivityType.query(organization).then(ok, fail);

            function ok(r) {
                vm.loadingActivityTypes = false;
                vm.activityTypes = r.items;
                vm.selectActivityType(vm.activityTypes, vm.activity);
            }

            function fail(r) {
                vm.loadingActivityTypes = false;
                vm.alert('danger', 'Failed to load activity types');
            }
        }

        function selectOrganization(organizations, activity) {
            if (activity.id) {
                angular.forEach(organizations, function (value, key) {
                    if (activity.id == value.id) {
                        vm.organization = value;
                    }
                });
            } else {
                vm.organization = organizations[0]; //todo: remove this, change to {}
            }
            vm.loadActivityTypes(vm.organization);
        }

        function selectActivityType(activityTypes, activity) {
            if (activity.activityType && activity.activityType.id) {
                angular.forEach(activityTypes, function (value, key) {
                    if (activity.activityType.id == value.id) {
                        activity.activityType = value;
                    }
                });
            }
        }

        function save() {
            Activity.add(vm.activity).then(ok, fail);
            function ok(r) {
                vm.alert('info', 'Activity created!');
                vm.activity = r;
                vm.selectActivityType(vm.activityTypes, vm.activity);
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }

        function reset() {
        }
    }

})(angular);