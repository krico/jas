/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivityTypesController', AdminActivityTypesController);

    function AdminActivityTypesController($location, $routeParams, $filter, jasDialogs, ActivityType, organizations, activityTypes, toolbarContext) {

        var vm = this;

        vm.organizations = organizations.items;

        if ($routeParams.organizationId) {
            setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            organizationSelected(vm.organizations[0]);
        }

        vm.activityTypes = activityTypes.items;
        vm.organizationSelected = organizationSelected;

        vm.selectActivityType = selectActivityType;
        vm.addActivityType = addActivityType;
        vm.viewActivityType = viewActivityType;
        vm.removeActivityType = removeActivityType;
        vm.getPreviewStyle = getPreviewStyle;

        var $translate = $filter('translate');

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
                var translation = $translate('ACTIVITY_TYPE_REMOVED');
                jasDialogs.success(translation);
                vm.activityTypes.splice(vm.activityTypes.indexOf(activityType), 1);
                vm.selectActivityType(null);
            });
        }

        function addActivityType() {
            $location.path('/admin/activity-type').search('organizationId', vm.organization.id);
        }

        function getPreviewStyle(color) {
            return {'backgroundColor': color};
        }

        function selectActivityType(activityType) {
            if (activityType && toolbarContext.contextEnabled()) {
                var actions = [
                    {
                        type: 'edit',
                        action: function () {
                            viewActivityType(activityType);
                        }
                    },
                    {
                        type: 'bin',
                        action: function () {
                            removeActivityType(activityType);
                        }
                    }];
                vm.selection = activityType;
                toolbarContext.setContext(actions);
            } else {
                delete vm.selection;
                toolbarContext.clearContext();
            }
        }
    }

}(window.angular));