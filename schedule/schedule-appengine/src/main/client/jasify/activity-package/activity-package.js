(function (angular) {

    angular.module('jasify.activityPackage').controller('ActivityPackageController', ActivityPackageController);

    function ActivityPackageController($log, ActivityPackage, organizations) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.organizationSelected = organizationSelected;
        vm.activityPackages = [];

        function organizationSelected(organization) {
            ActivityPackage.query(organization).then(ok, nok);

            function ok(resp) {
                $log.debug(angular.toJson(resp))
                vm.activityPackages = resp.items;
            }

            function nok(reason) {
                $log.debug('FAIL: ' + reason);
            }
        }
    }
})(angular);