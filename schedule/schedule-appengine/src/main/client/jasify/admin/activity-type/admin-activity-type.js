/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityTypeController', AdminActivityTypeController);

    function AdminActivityTypeController($location, $filter, getContrast,
                                         jasDialogs, aButtonController, ActivityType,
                                         activityType, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.activityType = activityType;
        vm.updateTagStyle = updateTagStyle;
        vm.updateTagStyle(vm.activityType.colourTag);

        initOrganization();

        vm.saveOrUpdate = saveOrUpdate;

        var $translate = $filter('translate');

        function saveOrUpdate() {

            var activityTypeToSave = angular.copy(vm.activityType),
                promise;

            if (activityTypeToSave.id) {
                promise = ActivityType.update(activityTypeToSave);
            } else {
                promise = ActivityType.add(vm.organization, activityTypeToSave);
            }

            vm.saveBtn.start(promise);
            promise.then(ok, fail);

            function ok(result) {
                if (activityTypeToSave.id) {
                    vm.activityType = result;
                    var activityTypeUpdatedTranslation = $translate('ACTIVITY_TYPE_UPDATED');
                    jasDialogs.success(activityTypeUpdatedTranslation);
                } else {
                    $location.search({});

                    if (result !== null) {
                        var activityTypeCreatedTranslation = $translate('ACTIVITY_TYPE_CREATED');
                        jasDialogs.success(activityTypeCreatedTranslation);
                        $location.path('/admin/activity-type/' + result.id);
                    } else {
                        var activityTypeNotCreatedTranslation = $translate('ACTIVITY_TYPE_NOT_CREATED');
                        jasDialogs.warning(activityTypeNotCreatedTranslation);
                        $location.path("/admin/activity-types");
                    }
                }
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
            }
        }

        function initOrganization() {

            if (!vm.organizations || vm.organizations.length === 0) {
                return;
            }

            if (vm.activityType.id) {
                vm.organization = _.find(
                    vm.organizations,
                    {id: vm.activityType.organizationId}
                );
            } else {
                if (vm.organizations.length === 1) {
                    vm.organization = vm.organizations[0];
                } else {
                    vm.organization = _.find(vm.organizations, {id: $location.search().organizationId});
                }
            }
        }

        function updateTagStyle(color) {
            vm.tagStyle = {
                'backgroundColor': color || 'white',
                color: getContrast.compute(color || 'white')
            };
        }
    }

}(window.angular));