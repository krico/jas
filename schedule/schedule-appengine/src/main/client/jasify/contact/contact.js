(function (angular) {

    angular.module('jasifyWeb').controller('ContactController', ContactController);

    function ContactController(Session, jasDialogs, $filter, $location, aButtonController, ContactMessage) {
        var vm = this;
        vm.sendBtn = aButtonController.createSend();
        vm.inProgress = false;
        vm.userId = Session.userId;
        vm.send = send;
        vm.contact = {};

        var $translate = $filter('translate');

        function send() {
            var promise = ContactMessage.send(vm.contact);

            vm.sendBtn.start(promise);
            promise.then(ok, fail);

            function ok(r) {
                var sentTranslation = $translate('SENT');
                jasDialogs.success(sentTranslation);
                $location.path('/contact/sent');
            }

            function fail(result) {
                var failedPleaseRetryTranslation = $translate('FAILED_PLEASE_RETRY');
                jasDialogs.resultError(failedPleaseRetryTranslation, result);
            }
        }
    }
})(angular);