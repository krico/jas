(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackagesController', AdminActivityPackagesController);

    function AdminActivityPackagesController($log, $routeParams, $location, organizations, activityPackages, ActivityPackage) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.activityPackages = activityPackages.items;
        vm.alert = alert;
        vm.alerts = [];
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.organizationSelected = organizationSelected;
        vm.addActivityPackage = addActivityPackage;
        vm.viewActivityPackage = viewActivityPackage;
        vm.remove = remove;

        vm.setSelectedOrganization($routeParams.organizationId);
        $location.search('organizationId', null);

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }

        function addActivityPackage() {
            if (!(vm.organization && vm.organization.id)) {
                vm.alert('warning', 'You must select an organization first');
                return;
            }

            $location.path('/admin/activity-package').search('organizationId', vm.organization.id);
        }


        function viewActivityPackage(id) {
            $location.path('/admin/activity-package/' + id);
        }

        function remove(id) {
            ActivityPackage.remove(id).then(ok, fail);
            function ok(r) {
                vm.alert('warning', 'ActivityPackage removed!');
                var newAP = [];
                angular.forEach(vm.activityPackages, function (value, key) {
                    if (id != value.id) {
                        this.push(value);
                    }
                }, newAP);
                vm.activityPackages = newAP;
            }
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