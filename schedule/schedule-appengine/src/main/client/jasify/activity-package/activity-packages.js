(function (angular) {

    angular.module('jasify.activityPackage').controller('ActivityPackagesController', ActivityPackagesController);

    function ActivityPackagesController($log, $location, $routeParams, organizations, activityPackages) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.organizationSelected = organizationSelected;
        vm.activityPackages = activityPackages.items;
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.bookIt = bookIt;
        vm.organization = {};

        vm.setSelectedOrganization($routeParams.organizationId);

        function setSelectedOrganization(organizationId) {
            if (organizationId) {
                angular.forEach(vm.organizations, function (value, key) {
                    if (organizationId == value.id) {
                        vm.organization = value;
                    }
                });
            }
        }

        function organizationSelected(organization) {
            $location.path('/activity-packages/' + organization.id);
        }

        function bookIt(activityPackage) {
            $location.path('/activity-package/' + activityPackage.id);
        }
    }
})(angular);