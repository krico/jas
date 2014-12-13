/**
 * Created by krico on 12/11/14.
 *
 * We keep the admin controllers separate so that only admins actually need to load them.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers');

jasifyScheduleControllers.controller('AdminUsersCtrl', ['$scope', '$location', 'User',
    function ($scope, $location, User) {
        $scope.sort = 'DESC';
        $scope.page = 1;
        $scope._perPage = 10;
        $scope.total = 0;
        $scope.numPages = 1;
        $scope.maxSize = 4;
        $scope.users = [];
        $scope.searchBy = 'name';
        $scope.query = '';
        $scope.regex = false;

        $scope.pageChanged = function () {
            var q;

            if ($scope.regex) {
                q = $scope.query;
            } else {
                q = RegExp.quote($scope.query);
            }

            $scope.users = User.query({
                    page: $scope.page,
                    size: $scope._perPage,
                    sort: $scope.sort,
                    field: $scope.searchBy,
                    query: q
                },
                function (data, h) {
                    var t = h('X-Total');
                    if (t != $scope.total)
                        $scope.total = t;
                },
                function (response) {
                    $scope.total = 0;
                    //TODO: show error
                });
        };

        $scope.typeChanged = function () {
            if ($scope.query) {
                $scope.queryChanged();
            }
        };

        $scope.queryChanged = function () {
            $scope.page = 1;
            $scope.pageChanged();
        };

        $scope.perPage = function (newValue) {
            if (angular.isDefined(newValue)) {
                var old = $scope._perPage;
                if (old != newValue) {
                    /* stay on the same record */
                    $scope.page = 1 + ((($scope.page - 1) * old) / newValue);

                    $scope._perPage = newValue;
                    $scope.pageChanged()
                }
            }
            return $scope._perPage;

        };

        $scope.viewUser = function (id) {
            $location.path('/admin/user/' + id);
        };

        $scope.pageChanged();
    }]);

jasifyScheduleControllers.controller('AdminUserCtrl', ['$scope', '$routeParams', '$modal', 'User', 'Auth',
    function ($scope, $routeParams, $modal, User, Auth) {
        $scope.user = null;
        $scope.pw = {};
        $scope.create = false;
        $scope.loading = true;

        $scope.alerts = [];
        $scope.forms = {};

        $scope.alert = function (t, m) {
            $scope.alerts.push({type: t, msg: m});
        };

        $scope.save = function () {

            $scope.loading = true;

            $scope.user.$save(function () {
                $scope.loading = false;
                if ($scope.forms.userForm) {
                    $scope.forms.userForm.$setPristine();
                }

                $scope.alert('success', 'User updated successfully (' + new Date() + ')');

            });
        };

        $scope.changePassword = function () {

            $scope.loading = true;

            Auth.changePassword($scope.user, "", $scope.pw.newPassword,
                // success
                function (data) {
                    $scope.loading = false;
                    if ($scope.forms.passwordForm) {
                        $scope.forms.passwordForm.$setPristine();
                    }
                    var jr = angular.fromJson(data);
                    if (jr.ok) {
                        $scope.alert('success', 'Password changed (' + new Date() + ')');
                        $scope.pw = {};
                    } else {
                        $scope.alert('danger', 'Password change failed: ' + jr.nokText + ' (' + new Date() + ')');
                    }
                },
                // failure
                function (data) {
                    $scope.loading = false;
                    if ($scope.forms.passwordForm) {
                        $scope.forms.passwordForm.$setPristine();
                    }
                    $scope.forms.passwordForm.$setPristine();
                    $scope.alert('danger', 'Password change failed (' + new Date() + ')');

                }
            );
        };

        $scope.createUser = function () {

            $scope.loading = true;
            $scope.user.confirmPassword = $scope.user.password;

            $scope.user.$save(
                //success
                function (value, responseHeaders) {
                    $scope.alert('success', 'User creation succeeded!');
                    $scope.create = false;
                    $scope.loading = false;
                },
                //error
                function (resp) {

                    $scope.loading = false;

                    $scope.alert('danger', 'User creation failed!');
                });
        };


        $scope.reset = function () {

            $scope.loading = true;

            if ($scope.forms.userForm) {
                $scope.forms.userForm.$setPristine();
            }

            if ($routeParams.id) {
                $scope.user = User.get({id: $routeParams.id}, function () {
                    $scope.loading = false;
                });
            } else {
                $scope.user = new User();
                $scope.create = true;
                $scope.loading = false;
            }
        };

        $scope.reset();

    }]);