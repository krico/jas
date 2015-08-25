(function (angular) {
    "use strict";

    angular.module('jasify.admin').controller('AdminHistoriesController', AdminHistoriesController);

    function AdminHistoriesController($log, $scope, History, histories) {
        var vm = this;
        vm.allHistories = histories.items;
        vm.histories = [];
        vm.historyTypes = [];
        vm.detectTypes = detectTypes;
        vm.historyType = false;
        vm.selectHistoryType = selectHistoryType;
        vm.getHistory = getHistory;
        vm.queryChanged = queryChanged;
        vm.init = init;

        // Client side pagination. Need this to simulate server side filtering
        vm.queryHistories = [];
        vm.displayHistories = [];

        vm.pageChanged = pageChanged;
        vm.perPage = perPage;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        vm.init();

        function init() {
            if (vm.allHistories) {
                // TODO: We want to display the history in descending 'created' order but we can not use orderBy because of how we use the various arrays
                vm.allHistories.reverse();
            }
            vm.histories = vm.allHistories;
            vm.detectTypes();
            vm.queryChanged();
            // vm.pageChanged();
        }
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
            vm.queryChanged();
        }

        function queryChanged() {
            if (vm.query) {
                vm.queryHistories = vm.histories.filter(function (el) {
                    return el.description.indexOf(vm.query) != -1;
                });
            } else {
                vm.queryHistories = vm.histories;
            }
            vm.pagination.total = vm.queryHistories.length;
            getHistory();
        }

        function getHistory() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.displayHistories = vm.queryHistories.slice(offset, offset + limit);
            vm.pagination.total = vm.queryHistories.length;
        }

        function pageChanged() {
            vm.getHistory();
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }
    }

})(angular);