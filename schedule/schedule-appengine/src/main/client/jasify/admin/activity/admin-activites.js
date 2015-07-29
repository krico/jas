/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivitiesController', AdminActivitiesController);

    function AdminActivitiesController($location, $routeParams, $filter, jasDialogs, Activity, organizations, activities, toolbarContext) {

        var vm = this;

        vm.organizations = organizations.items;

        if ($routeParams.organizationId) {
            setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            organizationSelected(vm.organizations[0]);
        }

        vm.activities = activities.items;
        vm.organizationSelected = organizationSelected;

        vm.addActivity = addActivity;
        vm.viewActivity = viewActivity;
        vm.removeActivity = removeActivity;
        vm.selectActivity = selectActivity;

        vm.viewSubscribers = viewSubscribers;
        vm.addSubscriber = addSubscriber;

        var $translate = $filter('translate');

        function setSelectedOrganization(organizationId) {
            vm.organization = _.find(vm.organizations, {id: organizationId});
        }

        function organizationSelected(organization) {
            $location.path('/admin/activities/' + organization.id);
        }

        function viewActivity(activity) {
            $location.path('/admin/activity/' + activity.id);
        }

        function removeActivity(activity) {
            Activity.remove(activity.id).then(function () {
                var translation = $translate('ACTIVITY_REMOVED');
                jasDialogs.success(translation)
                vm.activities.splice(vm.activities.indexOf(activity), 1);
                ;
                vm.selectActivity(null);
            });
        }

        function addActivity() {
            $location.path('/admin/activity').search('organizationId', vm.organization.id);
        }

        function viewSubscribers(id) {
            $location.path('/admin/activities/' + id + '/subscribers');
        }

        function addSubscriber(id) {
            $location.path('/admin/activities/' + id + '/subscribe');
        }

        function selectActivity(activity) {
            if (activity && toolbarContext.contextEnabled()) {
                var actions = [
                    {
                        type: 'edit',
                        action: function () {
                            viewActivity(activity);
                        }
                    },
                    {
                        type: 'bin',
                        action: function () {
                            removeActivity(activity);
                        }
                    }];
                vm.selection = activity;
                toolbarContext.setContext(actions);
            } else {
                delete vm.selection;
                toolbarContext.clearContext();
            }
        }
    }

}(window.angular));