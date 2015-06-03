(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminOrganizationsController', AdminOrganizationsController);

    function AdminOrganizationsController($location, Organization, Auth, organizations, toolbarContext) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.organization = {};
        vm.alerts = [];
        vm.reload = reload;
        vm.remove = remove;
        vm.update = update;
        vm.add = add;
        vm.viewOrganization = viewOrganization;
        vm.alert = alert;
        vm.isAdmin = isAdmin;
        vm.selectRow = selectRow;

        function alert(t, m) {
            vm.alerts.push({type: t, msg: m});
        }


        function reloadHandler(r) {
            vm.reload();
        }

        function errorHandler(resp) {
            alert('danger', 'Operation failed: (' + resp.status + ") '" + resp.statusText + "'");
        }

        function add(organization) {
            Organization.add(organization).then(ok, errorHandler);
            function ok(r) {
                vm.organization = {};
                vm.alert('success', 'Organization [' + r.name + '] added!');
                vm.reload();
            }
        }

        function update(org) {
            org.description += " Updated: " + new Date().toTimeString();
            Organization.update(org).then(ok, errorHandler);
        }

        function selectRow(organization) {
            if (toolbarContext.contextEnabled()) {
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