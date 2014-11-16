/**
 * Created by krico on 12/11/14.
 *
 * We keep the admin controllers separate so that only admins actually need to load them.
 */
var jasifyScheduleControllers = angular.module('jasifyScheduleControllers');

jasifyScheduleControllers.controller('AdminUsersCtrl', ['$scope', '$location', 'User', 'Modal',
    function ($scope, $location, User, Modal) {
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
                    Modal.showError("Server error", "The server responded with an error...")
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

jasifyScheduleControllers.controller('AdminUserCtrl', ['$scope', '$routeParams', '$alert', '$modal', 'User',
    function ($scope, $routeParams, $alert, $modal, User) {
        $scope.user = null;
        $scope.create = false;
        $scope.userForm = null;
        $scope.loading = true;

        $scope.save = function () {

            $scope.loading = true;

            $scope.user.$save()
                .then(function () {

                    $scope.loading = false;

                    $scope.userForm.$setPristine();

                    $alert({
                        title: 'User updated successfully (' + new Date() + ')',
                        container: '#alert-container',
                        type: 'success',
                        show: true
                    });

                });
        };

        $scope.createUser = function () {

            $scope.loading = true;

            $scope.user.$save(
                //success
                function (value, responseHeaders) {
                    $alert({
                        title: 'User creation succeeded!',
                        container: '#alert-container',
                        type: 'success',
                        show: true
                    });
                    $scope.create = false;
                    $scope.loading = false;
                },
                //error
                function (resp) {

                    $scope.loading = false;

                    $alert({
                        title: 'User creation failed!',
                        content: (resp.statusText ? resp.statusText : "Unknown") + " (" + resp.status + ")",
                        container: '#alert-container',
                        type: 'danger',
                        show: true
                    });
                });
        };

        $scope.pwModal = null;

        $scope.setPassword = function () {
            if ($scope.pwModal) {
                $scope.pwModal.$hide();
            } else {
                $scope.pwModal = $modal({
                    scope: $scope,
                    template: 'views/modal/password.html',
                    animation: 'am-fade-and-scale',
                    show: true
                });
            }
        };

        $scope.reset = function () {

            $scope.loading = true;

            if ($scope.userForm)
                $scope.userForm.$setPristine();

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