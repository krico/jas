(function (angular) {

    angular.module('jasifyWeb').controller('AdminActivityController', AdminActivityController);

    function AdminActivityController($location, ActivityType, Activity, activity, organizations) {
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

        vm.isRepeatOpen = false;
        vm.openRepeat = openRepeat;
        vm.repeatType = 'No';
        vm.repeatDays = {};
        vm.repeatDate = null;

        vm.organizations = organizations.items;
        vm.selectOrganization = selectOrganization;
        vm.selectActivityType = selectActivityType;
        vm.loadActivityTypes = loadActivityTypes;
        vm.hasActivityTypes = hasActivityTypes;
        vm.alert = alert;
        vm.update = update;
        vm.create = create;
        vm.reset = reset;
        vm.back = back;

        vm.selectOrganization(vm.organizations, vm.activity, $location.search().organizationId);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function back() {
            var orgId = null;
            if (vm.activity.activityType && vm.activity.activityType.organizationId) {
                orgId = vm.activity.activityType.organizationId;
            } else if (vm.organization.id) {
                orgId = vm.organization.id;
            }

            if (orgId === null) {
                $location.path("/admin/activities");
            } else {
                $location.path("/admin/activities/" + orgId); //TODO: path orgID to activity/X
            }
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

        function openRepeat($event) {
            $event.preventDefault();
            $event.stopPropagation();

            vm.isRepeatOpen = true;
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

        function selectOrganization(organizations, activity, organizationId) {
            if (activity.activityType && activity.activityType.organizationId) {
                angular.forEach(organizations, function (value, key) {
                    if (activity.activityType.organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            } else if (organizationId) {
                angular.forEach(organizations, function (value, key) {
                    if (organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            }
            if (vm.organization && vm.organization.id) {
                vm.loadActivityTypes(vm.organization);
            }
        }

        function selectActivityType(activityTypes, activity) {

            if (activity.activityType && activity.activityType.id) {
                angular.forEach(activityTypes, function (value, key) {
                    if (activity.activityType.id == value.id) {
                        vm.activity.activityType = value;
                    }
                });
            }
        }

        function create() {
            Activity.add(vm.activity).then(ok, fail);

            function ok(r) {
                vm.alert('info', 'Activity created!');
                // TODO This should be server side work
                if (vm.repeatType == "Weekly") {
                    while (vm.repeatDate && vm.activity.start.getTime() < vm.repeatDate.getTime() && vm.activity.finish.getTime() < vm.repeatDate.getTime()) {
                        vm.activity.start.setDate(vm.activity.start.getDate() + 1);
                        vm.activity.finish.setDate(vm.activity.finish.getDate() + 1);
                        if (vm.repeatDays[vm.activity.start.getDay()]) {
                            Activity.add(vm.activity).then(ok, fail);
                            return;
                        }
                    }
                }
                $location.path('/admin/activity/' + r.id);
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }

        function update() {
            Activity.update(vm.activity).then(ok, fail);
            function ok(r) {
                vm.alert('info', 'Activity updated!');
                vm.activity = r;
                vm.selectActivityType(vm.activityTypes, vm.activity);
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }

        function reset() {
            Activity.get(vm.activity.id).then(ok, fail);
            vm.activity = {};
            function ok(r) {
                vm.activity = r;
                vm.selectOrganization(vm.organizations, vm.activity, $location.search().organizationId);
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
            }
        }
    }

})(angular);