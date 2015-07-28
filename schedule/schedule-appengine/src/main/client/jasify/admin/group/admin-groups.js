(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminGroupsController', AdminGroupsController);

    function AdminGroupsController($location, $filter, jasDialogs, Group, groups) {
        var vm = this;
        vm.groups = groups.items;
        vm.group = {};
        vm.alerts = [];
        vm.reload = reload;
        vm.remove = remove;
        vm.update = update;
        vm.add = add;
        vm.viewGroup = viewGroup;

        var $translate = $filter('translate');

        function errorHandler(resp) {
            var translation = $translate('FAILED_PLEASE_RETRY');
            jasDialogs.resultError(translation, resp);
        }

        function add(group) {
            Group.add(group).then(ok, errorHandler);
            function ok() {
                vm.group = {};
                var translation = $translate('GROUP_CREATED');
                jasDialogs.success(translation);
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
                var translation = $translate('GROUP_REMOVED');
                jasDialogs.success(translation);
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