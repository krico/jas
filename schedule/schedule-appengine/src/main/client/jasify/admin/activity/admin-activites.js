/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminActivitiesController', AdminActivitiesController);

    function AdminActivitiesController($location, $routeParams, Activity, organizations, activities) {

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

        vm.viewSubscribers = viewSubscribers;
        vm.addSubscriber = addSubscriber;

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
                vm.activities.splice(vm.activities.indexOf(activity), 1);
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
    }

}(window.angular));