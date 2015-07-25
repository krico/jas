/*global window */
(function (angular, _) {

    angular.module('jasify.admin').controller('AdminActivityPackageController', AdminActivityPackageController);

    function AdminActivityPackageController(moment, $location, $filter, jasDialogs, aButtonController, ActivityPackage, Activity, organizations, activityPackage, activityPackageActivities) {

        var vm = this;
        vm.organization = null;

        vm.validUntilOptions = {
            minDate: moment()
        };
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

        var $translate = $filter('translate');

        vm.reset(true);

        vm.selectOrganization(vm.organizations, vm.activityPackage, $location.search().organizationId);

        function validateItemsCount() {

            var numSelectedActivities = (vm.selectedActivities || []).length;

            if (numSelectedActivities === 0) {
                var noActivitiesSelectedTranslation = $translate('SELECT_AT_LEAST_ONE_ACTIVITY');
                jasDialogs.warning(noActivitiesSelectedTranslation);
                return false;
            }

            if (numSelectedActivities < vm.activityPackage.itemCount) {
                var tooManyActivitesSelectedTranslation = $translate('ITEM_COUNT_LESS_THEN_SELECTED_ACTIVITIES', {value: vm.activityPackage.itemCount});
                jasDialogs.warning(tooManyActivitesSelectedTranslation);
                return false;
            }

            return true;
        }

        function update() {

            if (!validateItemsCount()) {
                return;
            }

            setValidUntilDate(vm.activityPackage, vm.selectedActivities);

            var promise = ActivityPackage.update(vm.activityPackage, vm.selectedActivities).then(ok, fail);
            vm.saveBtn.start(promise);

            function ok() {
                activityPackage = angular.copy(vm.activityPackage);
                activityPackageActivities = angular.copy(vm.selectedActivities);
                makePristine();
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function create() {

            if (!validateItemsCount()) {
                return;
            }

            vm.activityPackage.organizationId = vm.organization.id;

            setValidUntilDate(vm.activityPackage, vm.selectedActivities);

            ActivityPackage.add(vm.activityPackage, vm.selectedActivities).then(ok, fail);

            function ok(resp) {
                jasDialogs.success('Activity Package was created.');

                $location.search({});
                $location.path('/admin/activity-package/' + resp.id);
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
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

        function setValidUntilDate(activityPackage, activities) {
            if (!activityPackage.validUntil) {
                activityPackage.validUntil = moment(_.max(_.map(activities, function (activity) {
                    return new Date(activity.start).getTime();
                }))).format();
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
                Activity.query({organizationId: organization.id, fromDate: new Date().toISOString()}).then(ok, fail);
            }

            function ok(resp) {
                vm.activities = resp.items;
                vm.sortActivityArray(vm.activities);
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }
    }

})(window.angular, window._);