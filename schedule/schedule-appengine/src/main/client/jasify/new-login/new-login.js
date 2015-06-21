(function (angular) {

    'use strict';

    angular.module('jasifyWeb').controller('LoginPageController', function ($mdDialog, $rootScope, $location, $window,
                                                                            localStorageService, AUTH_EVENTS, Auth, BrowserData) {
        var vm = this;

        vm.recoverPassword = recoverPassword;
        vm.forgotMode = forgotMode;
        vm.createAccountMode = createAccountMode;
        vm.signInMode = signInMode;

        $rootScope.$on(AUTH_EVENTS.loginSuccess, onLoginSuccess);
        $rootScope.$on(AUTH_EVENTS.loginFailed, onLoginFailed);
        $rootScope.$on(AUTH_EVENTS.accountCreated, onAccountCreated);

        restore();

        function onLoginSuccess() {
            var backPage = localStorageService.get('loginBackPath');
            localStorageService.remove('loginBackPath');
            if (backPage) {
                $window.location = "/#" + backPage;
            } else {
                $window.location = "/";
            }
        }

        function onLoginFailed() {
            $mdDialog.show(
                $mdDialog.alert()
                    .parent(angular.element(document.body))
                    .title('Sing In Failed')
                    .content('Email/Username and password did not match. Please try again.')
                    .ariaLabel('Sing In Failed')
                    .ok('OK')
            );
        }

        function onAccountCreated() {
            $mdDialog.show(
                $mdDialog.alert()
                    .parent(angular.element(document.body))
                    .title('Account Created')
                    .content('Account was created. Now you can start using Jasify.')
                    .ariaLabel('Sign Up Success')
                    .ok('OK')
            ).then(function () {
                    //noinspection JSPrimitiveTypeWrapperUsage
                    $window.location.href = "/";
                });
        }

        function restore() {
            if (BrowserData.getLoggedIn()) {
                Auth.restore().then(function () {
                    $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
                }, function () {
                    signInMode();
                    vm.ready = true;
                });
            } else {
                signInMode();
                vm.ready = true;
            }
        }

        function recoverPassword(forgotPasswordController) {
            forgotPasswordController.recover().then(function () {
                $mdDialog.show(
                    $mdDialog.alert()
                        .parent(angular.element(document.body))
                        .title('Password Reset')
                        .content('An email with instructions was sent to ' + forgotPasswordController.email)
                        .ariaLabel('Password Reset Success')
                        .ok('OK')
                ).then(function () {
                        vm.signInMode();
                    });
            }, function () {
                $mdDialog.show(
                    $mdDialog.alert()
                        .parent(angular.element(document.body))
                        .title('Password Reset')
                        .content('We could not find this email address. Try again.')
                        .ariaLabel('Password Reset Fail')
                        .ok('OK')
                );
            });
        }

        function forgotMode() {
            vm.mode = 'mode-password-assistance';
        }

        function signInMode() {
            vm.mode = 'mode-sign-in';
        }

        function createAccountMode() {
            vm.mode = 'mode-sign-up';
        }
    });
}(window.angular));