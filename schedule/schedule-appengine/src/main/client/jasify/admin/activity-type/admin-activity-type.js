/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityTypeController', AdminActivityTypeController);

    function AdminActivityTypeController($scope, $location,
                                     jasDialogs, aButtonController, ActivityType,
                                     activityType, organizations) {
        var vm = this;

        vm.saveBtn = aButtonController.createSave();
        vm.organizations = organizations.items;
        vm.activityType = activityType;

        initOrganization();

        vm.saveOrUpdate = saveOrUpdate;

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
                    jasDialogs.success('Activity Type updated.');
                } else {
                    $location.search({});

                    if (result != null) {
                        jasDialogs.success('Activity Type was created.');
                        $location.path('/admin/activity-type/' + result.id);
                    } else {
                        jasDialogs.warning('Activity Type was not created.');
                        $location.path("/admin/activity-types");
                    }
                }
            }

            function fail(r) {
                vm.alert('danger', 'Failed: ' + r.statusText);
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
                    vm.organization = _.find(vm.organizations, { id: $location.search().organizationId});
                }
            }
        }
    }

}(window.angular));