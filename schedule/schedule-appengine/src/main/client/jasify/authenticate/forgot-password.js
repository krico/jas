/* global window */
(function (angular) {

    'use strict';

    angular.module('jasify.authenticate').controller('ForgotPasswordController', ForgotPasswordController);

    function ForgotPasswordController($q, $location, Auth, BrowserData) {
        var vm = this;
        vm.email = '';
        vm.recover = recover;
        vm.again = again;
        vm.inProgress = false;
        vm.passwordSent = false;
        vm.failed = false;
        vm.forgotForm = {};
        vm.hasBack = hasBack;
        vm.back = back;

        function recover() {

            var dfd = $q.defer();

            vm.inProgress = true;
            Auth.forgotPassword(vm.email).then(ok, fail);

            function ok() {
                dfd.resolve();
                vm.inProgress = false;
                vm.passwordSent = true;
            }

            function fail() {
                dfd.reject();
                vm.inProgress = false;
                vm.failed = true;
            }

            return dfd.promise;
        }

        function again() {
            vm.failed = false;
        }

        function hasBack() {
            return BrowserData.isForgotPasswordOriginSet();
        }

        function back() {
            var path = BrowserData.getForgotPasswordOrigin();
            BrowserData.clearForgotPasswordOrigin();
            $location.path(path);
        }
    }
})(window.angular);