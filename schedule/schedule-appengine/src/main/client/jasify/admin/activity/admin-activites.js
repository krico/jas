(function (angular) {

    angular.module('jasify.admin').controller('AdminActivitiesController', AdminActivitiesController);

    function AdminActivitiesController($log, $location, $routeParams, Activity, organizations, activities) {
        var vm = this;

        vm.organizations = organizations.items;
        vm.organization = {};
        if (vm.organizations.length == 1) organizationSelected(vm.organizations[0]);
        vm.alerts = [];
        vm.activities = activities.items;
        vm.alert = alert;
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.organizationSelected = organizationSelected;
        vm.viewActivity = viewActivity;
        vm.viewOrganization = viewOrganization;
        vm.viewSubscribers = viewSubscribers;
        vm.addSubscriber = addSubscriber;
        vm.addActivity = addActivity;
        vm.remove = remove;

        if ($routeParams.organizationId) {
            vm.setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            vm.organizationSelected(vm.organizations[0])
        }

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function setSelectedOrganization(organizationId) {
            if (organizationId) {
                angular.forEach(vm.organizations, function (value, key) {
                    if (organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            }
        }

        function viewOrganization(id) {
            if (id)
                $location.path('/admin/organization/' + id);
        }

        function organizationSelected(org) {
            if (org.id) {
                $location.path('/admin/activities/' + org.id);
            } else {
                $location.path('/admin/activities');
            }
        }

        function viewActivity(id) {
            $location.path('/admin/activity/' + id);
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

        function remove(id) {
            Activity.remove(id).then(ok, fail);
            function ok(r) {
                vm.alert('warning', 'Activity removed!');
                var newA = [];
                angular.forEach(vm.activities, function (value, key) {
                    if (id != value.id) {
                        this.push(value);
                    }
                }, newA);
                vm.activities = newA;
            }
        }

        function fail(r) {
            vm.alert('danger', 'Failed: ' + r.statusText);
        }
    }

})(angular);