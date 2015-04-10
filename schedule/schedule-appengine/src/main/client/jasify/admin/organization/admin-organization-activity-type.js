(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminOrganizationActivityTypeController', AdminOrganizationActivityTypeController);

    function AdminOrganizationActivityTypeController($log, $modalInstance, ActivityType, organization) {
        var vm = this;

        vm.organization = organization;
        vm.activityType = {};
        vm.save = save;
        vm.cancel = cancel;

        function save() {
            ActivityType.add(vm.organization, vm.activityType).then(ok, fail);
            function ok(r) {
                $modalInstance.close(r);
            }

            function fail(r) {
                alert('Failed to add: ' + r.status + ' - ' + r.statusText);
            }
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }

})(angular);