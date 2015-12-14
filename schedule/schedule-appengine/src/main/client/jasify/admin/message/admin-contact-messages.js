(function (angular) {

    angular.module('jasify.admin').controller('AdminContactMessagesController', AdminContactMessagesController);

    function AdminContactMessagesController(messages, jasDialogs, $location, $filter, ContactMessage) {
        var vm = this;
        vm.viewMessage = viewMessage;
        vm.removeMessage = removeMessage;
        vm.init = init;
        vm.allMessages = messages.items;
        vm.mails = [];

        vm.pageChanged = pageChanged;
        vm.perPage = perPage;
        vm.pagination = {
            total: 0,
            page: 1,
            itemsPerPage: 10,
            numPages: 1,
            maxSize: 5
        };

        var $translate = $filter('translate');

        vm.init();

        function init() {
            if (vm.allMessages) {
                vm.allMessages.reverse();
            }
            vm.messages = vm.allMessages.slice(0, vm.pagination.itemsPerPage);
            vm.pagination.total = vm.allMessages.length;
        }

        function viewMessage(message) {
            $location.path('/admin/contact-message/' + message.id);
        }

        function removeMessage(message) {
            ContactMessage.remove(message.id).then(ok, fail);

            function ok(r) {
                var translation = $translate('MESSAGE_REMOVED');
                jasDialogs.success(translation);
                vm.allMessages.splice(vm.allMessages.indexOf(message), 1);
                vm.pagination.total = vm.allMessages.length;
                pageChanged();
            }

            function fail(result) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, result);
            }
        }

        function pageChanged() {
            var limit = vm.pagination.itemsPerPage;
            var offset = (vm.pagination.page - 1) * limit;

            vm.messages = vm.allMessages.slice(offset, offset + limit);
        }

        function perPage() {
            return vm.pagination.itemsPerPage;
        }
    }
})(angular);