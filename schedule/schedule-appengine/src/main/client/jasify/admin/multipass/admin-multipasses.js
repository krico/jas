(function (angular) {

    angular.module('jasify.admin').controller('AdminMultipassesController', AdminMultipassesController);

    function AdminMultipassesController(organizations, jasDialogs, multipasses, Multipass, $location, $routeParams, $filter) {
        var vm = this;

        vm.organizations = organizations.items;
        vm.organizationSelected = organizationSelected;
        vm.setSelectedOrganization = setSelectedOrganization;
        vm.addMultipass = addMultipass;
        vm.viewMultipass = viewMultipass;
        vm.removeMultipass = removeMultipass;
        vm.allMultipasses = multipasses.items;
        vm.multipasses = [];

        vm.pageChanged = pageChanged;
        vm.perPage = perPage;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        if ($routeParams.organizationId) {
            vm.setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            vm.organizationSelected(vm.organizations[0]);
        }

        var $translate = $filter('translate');

        vm.multipasses = vm.allMultipasses.slice(0, vm.pagination.itemsPerPage);
        vm.pagination.total = vm.allMultipasses.length;

        if ($routeParams.organizationId) {
            vm.setSelectedOrganization($routeParams.organizationId);
        } else if (vm.organizations.length > 0) {
            vm.organizationSelected(vm.organizations[0]);
        }

        function setSelectedOrganization(organizationId) {
            vm.organization = _.find(vm.organizations, {id: organizationId});
        }

        function organizationSelected(organization) {
            $location.path('/admin/multipasses/' + organization.id);
        }

        function addMultipass() {
            $location.path('/admin/multipass').search('organizationId', vm.organization.id);
        }

        function viewMultipass(multipass) {
            $location.path('/admin/multipass/' + multipass.id);
        }

        function removeMultipass(multipass) {
            Multipass.remove(multipass.id).then(ok, fail);

            function ok() {
                var translation = $translate('MULTIPASS_REMOVED');
                jasDialogs.success(translation);
                vm.multipasses.splice(vm.multipasses.indexOf(multipass), 1);
                vm.allMultipasses.splice(vm.allMultipasses.indexOf(multipass), 1);
                vm.pagination.total = vm.allMultipasses.length;
            }

            function fail(resp) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, resp);
            }
        }

        function pageChanged() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.multipasses = vm.allMultipasses.slice(offset, offset + limit);
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }
    }
})(angular);