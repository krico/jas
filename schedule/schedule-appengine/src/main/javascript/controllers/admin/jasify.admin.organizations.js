(function (angular) {
    "use strict";

    angular.module('jasifyScheduleControllers').controller('AdminOrganizationsController', AdminOrganizationsController);

    function AdminOrganizationsController($log, Organization, organizations) {
        var vm = this;
        vm.organizations = organizations.items;
        vm.lastResponse = '';
        vm.lastFailure = '';
        vm.reload = reload;
        vm.remove = remove;
        vm.update = update;
        vm.test = test;

        function ok(r) {
            vm.lastResponse = r;
            vm.reload();
        }

        function fail(m) {
            vm.lastFailure = m;
        }

        function test() {
            var organization = {
                name: 'Organization at ' + new Date().toISOString(),
                description: 'This organization was created as a test'
            };
            Organization.add(organization).then(ok, fail);

        }

        function update(org) {
            org.description += " Updated: " + new Date().toTimeString();
            Organization.update(org).then(ok, fail);
        }

        function remove(id) {
            Organization.remove(id).then(ok, fail);
        }

        function reload() {
            Organization.query().then(function (r) {
                vm.organizations = r.items;
            });
        }
    }

})(angular);