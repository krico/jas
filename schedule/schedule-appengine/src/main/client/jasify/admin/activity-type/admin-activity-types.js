/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityTypesController', AdminActivityTypesController);

    function AdminActivityTypesController($location, $routeParams, ActivityType, organizations, activityTypes) {

        var vm = this;

        vm.organizations = organizations.items;

        if ($routeParams.organizationId) {
            setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            organizationSelected(vm.organizations[0]);
        }

        vm.activityTypes = activityTypes.items;
        vm.organizationSelected = organizationSelected;

        vm.addActivityType = addActivityType;
        vm.viewActivityType = viewActivityType;
        vm.removeActivityType = removeActivityType;
        vm.getPreviewStyle = getPreviewStyle;

        function setSelectedOrganization(organizationId) {
            vm.organization = _.find(vm.organizations, {id: organizationId});
        }

        function organizationSelected(organization) {
            $location.path('/admin/activity-types/' + organization.id);
        }

        function viewActivityType(activityType) {
            $location.path('/admin/activity-type/' + activityType.id);
        }

        function removeActivityType(activityType) {
            ActivityType.remove(activityType.id).then(function () {
                vm.activityTypes.splice(vm.activityTypes.indexOf(activityType), 1);
            });
        }

        function addActivityType() {
            $location.path('/admin/activity-type').search('organizationId', vm.organization.id);
        }

        function getPreviewStyle(color) {
            return {'backgroundColor': color};
        }
    }

}(window.angular));