(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackageController', AdminActivityPackageController);

    function AdminActivityPackageController($log, $location, $scope, ActivityType, Activity, organizations, activityPackage) {
        var vm = this;
        vm.alerts = [];
        vm.organization = {};
        vm.activityPackage = activityPackage || {};
        vm.activities = [];
        vm.selectedActivities = [];
        vm.organizations = organizations.items;
        vm.alert = alert;
        vm.back = back;
        vm.selectOrganization = selectOrganization;
        vm.onOrganizationSelected = onOrganizationSelected;
        vm.deselectActivity = deselectActivity;
        vm.selectActivity = selectActivity;
        vm.filterSelected = filterSelected;
        vm.sortActivityArray = sortActivityArray;

        vm.update = update;
        vm.create = create;
        vm.reset = reset;

        vm.selectOrganization(vm.organizations, vm.activityPackage, $location.search().organizationId);

        function update() {
        }

        function create() {
        }

        function reset() {
        }

        function sortActivityArray(array) {
            array.sort(cmp);

            function cmp(activity1, activity2) {
                var a = activity1.start || new Date();
                var b = activity2.start || new Date();
                return a > b ? 1 : a < b ? -1 : 0;

            }
        }

        function selectActivity(activity) {
            vm.selectedActivities.push(activity);
            vm.sortActivityArray(vm.selectedActivites);
        }

        function filterSelected(value, index) {
            for (var i = 0; i < vm.selectedActivities.length; ++i) {
                if (vm.selectedActivities[i].id == value.id) return false;
            }
            return true;
        }

        function deselectActivity(activityId) {
            if (!activityId) return;

            var indices = [];
            vm.selectedActivities.forEach(function (value, index) {

                if (activityId == value.id) {
                    indices.push(index);
                }
            });
            indices.forEach(function (index) {
                vm.selectedActivities.splice(index, 1);
            });
        }

        function selectOrganization(organizations, activityPackage, organizationId) {
            if (activityPackage && activityPackage.organizationId) {
                angular.forEach(organizations, function (value, key) {
                    if (activityPackage.organizationId == value.id) {
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
                vm.onOrganizationSelected(vm.organization);
            }
        }

        function onOrganizationSelected(organization) {
            vm.activities = [];
            vm.selectedActivites = [];
            if (organization.id) {
                Activity.query({organizationId: organization.id}).then(ok, failed);
            }

            function ok(resp) {
                vm.activities = resp.items;
                vm.sortActivityArray(vm.activities);
            }

            function failed(reason) {
                vm.alert('danger', 'Failed: ' + angular.toJson(reason));
            }
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function back() {
            var orgId = null;
            if (vm.activityPackage && vm.activityPackage.organizationId) {
                orgId = vm.activityPackage.organizationId;
            } else if (vm.organization.id) {
                orgId = vm.organization.id;
            }

            if (orgId === null) {
                $location.path('/admin/activity-packages');
            } else {
                $location.path('/admin/activity-packages/' + orgId); //TODO: path orgID to activity/X
            }
        }
    }

})(angular);