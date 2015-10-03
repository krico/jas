(function (angular) {

    angular.module('jasify.admin').controller('AdminActivityPackagesController', AdminActivityPackagesController);

    function AdminActivityPackagesController($routeParams, $location, $filter, jasDialogs, organizations, activityPackages, ActivityPackage) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.activityPackages = activityPackages.items;
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.organizationSelected = organizationSelected;
        vm.addActivityPackage = addActivityPackage;
        vm.viewActivityPackage = viewActivityPackage;
        vm.remove = remove;

        var $translate = $filter('translate');

        if ($routeParams.organizationId) {
            vm.setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            vm.organizationSelected(vm.organizations[0]);
        }

        function addActivityPackage() {
            if (vm.organization && vm.organization.id) {
                $location.path('/admin/activity-package').search('organizationId', vm.organization.id);
            } else {
                $location.path('/admin/activity-package');
            }
        }

        function viewActivityPackage(id) {
            $location.path('/admin/activity-package/' + id);
        }

        function remove(id) {
            ActivityPackage.remove(id).then(ok, fail);
            function ok(r) {
                var activityPackageRemovedTranslation = $translate('ACTIVITY_PACKAGE_REMOVED');
                jasDialogs.success(activityPackageRemovedTranslation);

                var newAP = [];
                angular.forEach(vm.activityPackages, function (value, key) {
                    if (id != value.id) {
                        this.push(value);
                    }
                }, newAP);
                vm.activityPackages = newAP;
            }

            function fail(r) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, r);
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