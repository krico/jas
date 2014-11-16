/**
 * Created by krico on 02/11/14.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers', ['mgcrea.ngStrap']);

jasifyScheduleControllers.controller('NavbarCtrl', ['$scope', '$location', 'Auth',
    function ($scope, $location, Auth) {
        $scope.user = Auth.getCurrentUser();

        $scope.isActive = function (viewLocation) {
            return viewLocation === $location.path();
        };

        $scope.$watch(Auth.getCurrentUser, function (newValue, oldValue) {
            $scope.user = Auth.getCurrentUser();
        });
    }]);

jasifyScheduleControllers.controller('HomeCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        $scope.user = Auth.getCurrentUser();
    }]);

jasifyScheduleControllers.controller('SignUpCtrl', ['$scope', '$http', '$alert', '$location', '$tooltip', 'Util', 'User', 'Auth',
    function ($scope, $http, $alert, $location, $tooltip, Util, User, Auth) {

        $scope.usernameCheck = {};

        $scope.spinnerHidden = true;

        $scope.newUser = {}; //TODO: remove

        $scope.usernameTooltip = {"title" : "Username is required."};

        $scope.emailTooltip = {"title" : "Email is required."};

        $scope.passwordTooltip = {};

        $scope.confirmTooltip = {"title" : "The passwords do not match."};

        $scope.hasError = function (fieldName) {
            return Util.formFieldError($scope.signUpForm, fieldName);
        };

        $scope.hasSuccess = function (fieldName) {
            return Util.formFieldSuccess($scope.signUpForm, fieldName);
        };

        $scope.checkUsername = function () {
            if ($scope.user.name) {
                $scope.spinnerHidden = false;
                $scope.usernameCheck = User.checkUsername($scope.user.name,
                    //success
                    function (value, responseHeaders) {
                        $scope.usernameTooltip = "";
                    },
                    //error
                    function (httpResponse) {
                        //simulate a nok
                        $scope.usernameCheck = {nok: true, nokText: 'Communication error'};
                    });
            } else {
                $scope.spinnerHidden = true;
                $scope.usernameCheck = {};
                $scope.usernameTooltip = "Username is required."
            }
        };

        $scope.createUser = function () {
            $scope.newUser = User.save($scope.user,
                //success
                function (value, responseHeaders) {
                    $alert({
                        title: 'Registration succeeded!',
                        content: 'You were successfully registered. Your browser should be redirected shortly.',
                        container: '#alert-container',
                        type: 'success',
                        show: true
                    });
                    //Simulate a login
                    Auth.login($scope.user.name, $scope.user.password, function (message) {
                        $alert({
                            title: 'Login failed!',
                            content: "Funny, even though we just registered you, your login failed...",
                            container: '#alert-container',
                            type: 'danger',
                            show: true
                        });
                        $scope.newUser = {};
                    });
                },
                //error
                function (httpResponse) {
                    //simulate a nok
                    $scope.usernameCheck = {nok: true, nokText: 'Registration failed'};
                    $scope.newUser = {};
                    $alert({
                        title: 'Registration failed!',
                        content: "Registration failed, we don't really know why. Please change some fields and try again.",
                        container: '#alert-container',
                        type: 'danger',
                        show: true
                    });
                });
        };
    }]);

jasifyScheduleControllers.controller('LoginCtrl', ['$scope', '$alert', 'Util', 'Auth', 'Modal',
    function ($scope, $alert, Util, Auth, Modal) {

        $scope.user = {};

        $scope.credentials = {};

        $scope.hasError = function (fieldName) {
            return Util.formFieldError($scope.loginForm, fieldName);
        };

        $scope.hasSuccess = function (fieldName) {
            return Util.formFieldSuccess($scope.loginForm, fieldName);
        };

        $scope.login = function () {
            Auth.login($scope.credentials.name, $scope.credentials.password, function (reason) {
                $alert({
                    title: 'Login failed!',
                    content: reason,
                    container: '#alert-container',
                    type: 'warning',
                    show: true
                });

            });
        };

    }]);

jasifyScheduleControllers.controller('LogoutCtrl', ['$scope', 'Auth',
    function ($scope, Auth) {
        Auth.logout();
    }]);

jasifyScheduleControllers.controller('ProfileCtrl', ['$scope', '$alert', 'Auth', 'User',
    function ($scope, $alert, Auth, User) {
        $scope.user = {};
        $scope.save = function () {
            $scope.user.$save().then(function () {
                $alert({
                    title: 'Profile updated!',
                    container: '#alert-container',
                    type: 'success',
                    show: true
                });
                //TODO: We probably need to check for failures
                Auth.setCurrentUser($scope.user);
            });
        };
        $scope.reset = function () {
            $scope.user = User.get({id: Auth.getCurrentUser().id});
        };
        $scope.reset();
    }]);

jasifyScheduleControllers.controller('HelpCtrl', ['$scope',
    function ($scope) {
    }]);

jasifyScheduleControllers.controller('ContactUsCtrl', ['$scope',
    function ($scope) {
    }]);

