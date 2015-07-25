(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminOrganizationsController', AdminOrganizationsController);

    function AdminOrganizationsController($location, $filter, jasDialogs, Organization, Auth, organizations, toolbarContext) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.organization = {};
        vm.alerts = [];
        vm.reload = reload;
        vm.remove = remove;
        vm.update = update;
        vm.add = add;
        vm.viewOrganization = viewOrganization;
        vm.isAdmin = isAdmin;
        vm.selectOrganization = selectOrganization;

        var $translate = $filter('translate');

        function errorHandler(resp) {
            var translation = $translate('ORGANIZATION_NOT_CREATED');
            jasDialogs.warning(translation);
        }

        function add(organization) {
            Organization.add(organization).then(ok, errorHandler);
            function ok(r) {
                vm.organization = {};
                vm.reload();
                var translation = $translate('ORGANIZATION_CREATED');
                jasDialogs.success(translation);
            }
        }

        function update(org) {
            org.description += " Updated: " + new Date().toTimeString();
            Organization.update(org).then(ok, errorHandler);
        }

        function selectOrganization(organization) {
            if (organization && toolbarContext.contextEnabled()) {
                var actions = [
                    {
                        type: 'edit',
                        action: function () {
                            viewOrganization(organization.id);
                        }
                    },
                    {
                        type: 'bin',
                        action: function () {
                            remove(organization.id);
                        }
                    }];
                vm.selection = organization;
                toolbarContext.setContext(actions);
            } else {
                toolbarContext.clearContext();
                delete vm.selection;
            }
        }

        function remove(id) {
            Organization.remove(id).then(vm.reload);
        }

        function viewOrganization(id) {
            $location.path('/admin/organization/' + id);
        }

        function reload() {
            Organization.query().then(function (r) {
                vm.organizations = r.items;
            }, errorHandler);
        }

        function isAdmin() {
            return Auth.isAdmin();
        }
    }

})(angular);