(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackageController', AdminActivityPackageController);

    function AdminActivityPackageController($location, $scope, ActivityType, Activity, activity, organizations) {
        var vm = this;
        vm.alerts = [];
        vm.organization = {};
        vm.activityPackage = {};
        vm.organizations = organizations.items;
        vm.alert = alert;
        vm.back = back;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function back() {
            var orgId = null;
            if (vm.activityPackage && vm.activityPackage.organizationId) {
                orgId = vm.activityPackage.organizationId;
            } else if (vm.organization.id) {
                orgId = vm.organization.id;
            }

            if (orgId === null) {
                $location.path('/admin/activity-packages');
            } else {
                $location.path('/admin/activity-packages/' + orgId); //TODO: path orgID to activity/X
            }
        }
    }

})(angular);