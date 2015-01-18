(function (angular) {

    angular.module('jasifyScheduleControllers').controller('AdminActivitiesController', AdminActivitiesController);

    function AdminActivitiesController($location, Activity, organizations) {
        var vm = this;

        vm.organizations = organizations.items;
        vm.organization = organizations.items;
        vm.alerts = [];
        vm.activities = [];
        vm.alert = alert;
        vm.organizationSelected = organizationSelected;
        vm.viewActivity = viewActivity;
        vm.remove = remove;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function organizationSelected(org) {
            vm.activities = [];
            if (org.id) {
                Activity.query({organizationId: org.id}).then(ok, fail);
            }
            function ok(r) {
                vm.activities = r.items;
            }

        }

        function viewActivity(id) {
            $location.path('/admin/activity/' + id);
        }

        function remove(id) {
            Activity.remove(id).then(ok, fail);
            function ok(r) {
                vm.alert('warning', 'Activity removed!');
                vm.organizationSelected(vm.organization);
            }
        }

        function fail(r) {
            vm.alert('danger', 'Failed: ' + r.statusText);
        }
    }

})(angular);