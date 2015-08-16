(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminHistoriesController', AdminHistoriesController);

    function AdminHistoriesController($log, History, histories) {
        var vm = this;
        vm.allHistories = histories.items;
        vm.histories = histories.items;
        vm.historyTypes = [];
        vm.detectTypes = detectTypes;
        vm.historyType = false;
        vm.selectHistoryType = selectHistoryType;

        vm.detectTypes();

        /**
         * TODO: We are missing functionality to
         * TODO: 1) Select [startDate] and [endDate]
         * TODO: 2) Have means to reload the query (call History.query() like we do in admin.routes.js) with start/end dates
         */

        function detectTypes() {
            var seen = {};
            vm.historyTypes = [];
            angular.forEach(vm.allHistories, function (value, key) {
                if (!this[value.type]) {
                    this[value.type] = 1;
                    vm.historyTypes.push(value.type);
                }
            }, seen);
        }

        function selectHistoryType(t) {
            vm.historyType = t;
            var newList;
            if (t) {
                newList = [];
                angular.forEach(vm.allHistories, function (value) {
                    if (value.type == vm.historyType) {
                        this.push(value);
                    }
                }, newList);
            } else {
                newList = vm.allHistories;
            }
            vm.histories = newList;
        }

    }

})(angular);