(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackagesController', AdminActivityPackagesController);

    function AdminActivityPackagesController($log, $routeParams, $location, organizations, activityPackages) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.activityPackages = activityPackages.items;
        vm.alert = alert;
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.organizationSelected = organizationSelected;
        vm.addActivityPackage = addActivityPackage;
        vm.viewActivityPackage = viewActivityPackage;

        vm.setSelectedOrganization($routeParams.organizationId);
        $location.search('organizationId', null);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function addActivityPackage() {
            $location.path('/admin/activity-package').search('organizationId', vm.organization.id);
        }


        function viewActivityPackage(id) {
            $location.path('/admin/activity-package/' + id);
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

        function organizationSelected(org) {
            if (org.id) {
                $location.path('/admin/activity-packages/' + org.id);
            } else {
                $location.path('/admin/activity-packages');
            }
        }

    }
})(angular);