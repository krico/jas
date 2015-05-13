/*global window */
(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackageController', AdminActivityPackageController);

    function AdminActivityPackageController($location, jasDialogs, aButtonController, ActivityPackage, Activity, organizations, activityPackage, activityPackageActivities) {

        var vm = this;
        vm.organization = null;

        vm.saveBtn = aButtonController.createSave();
        vm.resetBtn = aButtonController.createReset();

        /* updated by the vm.reset() call */
        vm.activityPackage = {};
        /* updated by the vm.reset() call */
        vm.selectedActivities = [];

        vm.activities = [];
        vm.organizations = organizations.items;
        vm.selectOrganization = selectOrganization;
        vm.onOrganizationSelected = onOrganizationSelected;
        vm.deselectActivity = deselectActivity;
        vm.selectActivity = selectActivity;
        vm.filterSelected = filterSelected;
        vm.sortActivityArray = sortActivityArray;

        vm.save = function (activityPackage) {
            if (activityPackage.id) {
                vm.update();
            } else {
                vm.create();
            }
        };

        vm.update = update;
        vm.create = create;
        vm.reset = reset;

        vm.reset(true);

        vm.selectOrganization(vm.organizations, vm.activityPackage, $location.search().organizationId);

        function validateItemsCount() {

            var numSelectedActivities = (vm.selectedActivities || []).length;

            if (numSelectedActivities === 0) {
                jasDialogs.warning('You must select at least one activity.');
                return false;
            }

            if (numSelectedActivities < vm.activityPackage.itemCount) {
                jasDialogs.warning(
                    "Items Count specifies minimum number of activities in the package. " +
                    "You must select at least " + vm.activityPackage.itemCount + " activities or decrease Items Count.");
                return false;
            }

            return true;
        }

        function update() {

            if (!validateItemsCount()) {
                return;
            }

            var promise = ActivityPackage.update(vm.activityPackage, vm.selectedActivities).then(ok, fail);
            vm.saveBtn.start(promise);

            function ok() {
                activityPackage = angular.copy(vm.activityPackage);
                activityPackageActivities = angular.copy(vm.selectedActivities);

                makePristine();
            }

            function fail() {
                jasDialogs.error('Failed to update Activity Package. Please try again.');
            }
        }

        function create() {

            if (!validateItemsCount()) {
                return;
            }

            vm.activityPackage.organizationId = vm.organization.id;

            ActivityPackage.add(vm.activityPackage, vm.selectedActivities).then(function (resp) {
                jasDialogs.success('Activity Package was created.');
                $location.path('/admin/activity-package/' + resp.id);
            }, function () {
                jasDialogs.error('Failed to create Activity Package. Please try again.');
            });
        }

        function reset(initialReset) {

            if (activityPackage) {
                vm.activityPackage = angular.copy(activityPackage);
            } else {
                vm.activityPackage = {};
            }

            if (activityPackageActivities) {
                vm.selectedActivities = angular.copy(activityPackageActivities);
            } else {
                vm.selectedActivities = [];
            }
            makePristine();

            if (!initialReset) {
                vm.resetBtn.pulse();
            }
        }

        function makeDirty() {
            if (vm.activityPackageForm) {
                vm.activityPackageForm.$setDirty();
            }
        }

        function makePristine() {
            if (vm.activityPackageForm) {
                vm.activityPackageForm.$setPristine();
                vm.activityPackageForm.$setUntouched();
            }
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
            makeDirty();
            vm.selectedActivities.push(activity);
            vm.sortActivityArray(vm.selectedActivities);
        }

        function deselectActivity(activityId) {
            if (!activityId) return;

            makeDirty();

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

        function filterSelected(value, index) {
            for (var i = 0; i < vm.selectedActivities.length; ++i) {
                if (vm.selectedActivities[i].id == value.id) {
                    return false;
                }
            }
            return true;
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
            if (!(vm.activityPackage &&
                vm.activityPackage.organizationId &&
                vm.activityPackage.organizationId == organization.id)) {
                vm.selectedActivities = [];
            }
            if (organization.id) {
                Activity.query({organizationId: organization.id, fromDate: new Date().toISOString()}).then(ok, failed);
            }

            function ok(resp) {
                vm.activities = resp.items;
                vm.sortActivityArray(vm.activities);
            }

            function failed() {
                jasDialogs.error("Failed to get organization activities");
            }
        }
    }

})(window.angular);