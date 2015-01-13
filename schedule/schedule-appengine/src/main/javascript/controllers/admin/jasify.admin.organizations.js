(function (angular) {
    "use strict";

    angular.module('jasifyScheduleControllers').controller('AdminOrganizationsController', AdminOrganizationsController);

    function AdminOrganizationsController($location, Organization, organizations) {
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
                vm.alert('success', 'Organization [' + r.name + '] added!');
                vm.reload();
            }
        }

        function update(org) {
            org.description += " Updated: " + new Date().toTimeString();
            Organization.update(org).then(ok, errorHandler);
        }

        function remove(id) {
            Organization.remove(id).then(ok, errorHandler);
            function ok(){
                vm.alert('warning', 'Organization removed!');
                vm.reload();
            }
        }

        function viewOrganization(id){
            $location.path('/admin/organization/' + id);
        }

        function reload() {
            Organization.query().then(function (r) {
                vm.organizations = r.items;
            }, errorHandler);
        }
    }

})(angular);