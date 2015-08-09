(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminHistoriesController', AdminHistoriesController);

    function AdminHistoriesController($log, History, histories) {
        var vm = this;
        vm.histories = histories.items;
    }

})(angular);