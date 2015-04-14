(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminGroupsController', AdminGroupsController);

    function AdminGroupsController($location, Group, groups) {
        var vm = this;
        vm.groups = groups.items;
        vm.group = {};
        vm.alerts = [];
        vm.reload = reload;
        vm.remove = remove;
        vm.update = update;
        vm.add = add;
        vm.viewGroup = viewGroup;
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

        function add(group) {
            Group.add(group).then(ok, errorHandler);
            function ok(r) {
                vm.group = {};
                vm.alert('success', 'Group [' + r.name + '] added!');
                vm.reload();
            }
        }

        function update(org) {
            org.description += " Updated: " + new Date().toTimeString();
            Group.update(org).then(ok, errorHandler);
        }

        function remove(id) {
            Group.remove(id).then(ok, errorHandler);
            function ok(){
                vm.alert('warning', 'Group removed!');
                vm.reload();
            }
        }

        function viewGroup(id){
            $location.path('/admin/group/' + id);
        }

        function reload() {
            Group.query().then(function (r) {
                vm.groups = r.items;
            }, errorHandler);
        }
    }

})(angular);