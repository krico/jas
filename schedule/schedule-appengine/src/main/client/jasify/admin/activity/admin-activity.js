(function (angular) {

    angular.module('jasifyWeb').controller('AdminActivityController', AdminActivityController);

    angular.module('jasifyWeb').directive('datepickerPopup', function () {
        return {
            restrict: 'EAC',
            require: 'ngModel',
            link: function (scope, element, attr, controller) {
                //remove the default formatter from the input directive to prevent conflict
                controller.$formatters.shift();
            }
        };
    });

    function AdminActivityController($location, $scope, ActivityType, Activity, activity, organizations) {
        var vm = this;

        vm.dateFormat = 'dd MMM yyyy HH:mm';
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
        vm.repeatDetails = {};
        vm.repeatTypes = ["No", "Daily", "Weekly"];
        vm.repeatUntilTypes = ["Count", "Date"];
        vm.minStartDate = new Date(); // this seems wrong?

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
        vm.init = init;

        vm.selectOrganization(vm.organizations, vm.activity, $location.search().organizationId);

        vm.init();

        function init() {
            vm.activity.start = new Date();
            vm.activity.start.setMinutes(0, 0, 0);
            vm.activity.start.setHours(vm.activity.start.getHours() + 1);
            vm.activity.finish = new Date(vm.activity.start.getTime() + 60 * 60 * 1000);
            vm.repeatDetails.repeatType = vm.repeatTypes[0];
            vm.repeatDetails.repeatUntilType = vm.repeatUntilTypes[0];
            vm.repeatDetails.untilCount = 1;
            vm.repeatDetails.repeatEvery = 1;
            vm.repeatDetails.untilDate = new Date(vm.activity.finish);
        }

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
                if (vm.activityTypes.length == 1) {
                    vm.activity.activityType = vm.activityTypes[0];
                    vm.activity.description = vm.activityTypes[0].description;
                }
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

        $scope.$watch(
            // This function returns the value being watched.
            function () {
                return vm.activity.start;
            },
            // This is the change listener, called when the value returned from the above function changes
            function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    var offset = newValue.getTime() - oldValue.getTime();
                    vm.activity.finish = new Date(vm.activity.finish.getTime() + offset);
                }
            }
        );

        $scope.$watch(
            // This function returns the value being watched.
            function () {
                return vm.activity.finish;
            },
            // This is the change listener, called when the value returned from the above function changes
            function (newValue, oldValue) {
                if (newValue !== oldValue && vm.activity.finish.getTime() > vm.repeatDetails.untilDate.getTime()) {
                    vm.repeatDetails.untilDate = new Date(vm.activity.finish);
                }
            }
        );

        $scope.$watch(
            // This function returns the value being watched.
            function () {
                return vm.repeatType;
            },
            // This is the change listener, called when the value returned from the above function changes
            function (newValue, oldValue) {
                if (newValue !== oldValue && oldValue == "No") {
                    vm.repeatDetails.untilDate = new Date(vm.activity.finish.getTime());
                }
            }
        );
    }

})(angular);