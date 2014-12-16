describe('AdminControllers', function () {

    var $controller, $httpBackend, $rootScope;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
    }));

    beforeEach(inject(function (_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    describe('AdminUsersCtrl', function () {
        var $scope, controller, $location, User;

        beforeEach(inject(function (_$location_, _User_) {
            $location = _$location_;
            User = _User_;
        }));

        beforeEach(function () {
            $scope = $rootScope.$new();
            $httpBackend.expectGET('/user?field=name&page=1&query=&size=10&sort=DESC')
                .respond(200);
            controller = $controller('AdminUsersCtrl', {$scope: $scope, $location: $location, User: User});
            $httpBackend.flush();
        });

        it('can be instantiated', function () {
        });

        it('pageChanged passes query parameters and reads X-Total', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 5;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=5&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.pageChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(2);
            expect($scope.total).toEqual(50);
        });

        it('typeChanged does nothing if query is not set', function () {

            $scope.typeChanged();

        });

        it('typeChanged calls queryChanged if query is set', function () {

            $scope.query = 'foo';
            spyOn($scope, 'queryChanged');
            $scope.typeChanged();
            expect($scope.queryChanged).toHaveBeenCalled();

        });

        it('queryChanged moves back to page 1', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 5;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=1&query=foo&size=5&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.queryChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(1);
            expect($scope.total).toEqual(50);
        });

        it('perPage queries and stays on same record', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 4;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=4&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.pageChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(2);
            expect($scope.total).toEqual(50);

            //with 4 per page on page 2, we are on record 5
            //if we change _perPage to 2 we should end on page 3
            $httpBackend.expectGET('/user?field=email&page=3&query=foo&size=2&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.perPage(2);

            $httpBackend.flush();
        });

        it('view users goes to /admin/user/:id', function () {

            $scope.viewUser(555);
            expect($location.path()).toEqual('/admin/user/555');
        });

    });

    describe('AdminUserCtrl', function () {
        var $scope, controller, $routeParams, $modal, User, Auth;

        beforeEach(inject(function (_$routeParams_, _$modal_, _User_, _Auth_) {
            $routeParams = _$routeParams_;
            $modal = _$modal_;
            User = _User_;
            Auth = _Auth_;
        }));

        beforeEach(function () {
            $scope = $rootScope.$new();
            $httpBackend.expectGET('/user/555')
                .respond(200, {id: 555, name: 'test'});

            $routeParams.id = 555;

            controller = $controller('AdminUserCtrl', {
                $scope: $scope,
                $routeParams: $routeParams,
                User: User,
                Auth: Auth
            });

            $httpBackend.flush();

        });

        it('reads routeParams and loads user', function () {

            expect($scope.create).toBe(false);

            expect($scope.user.id).toEqual(555);
            expect($scope.user.name).toEqual('test');

        });

        it('can handle alerts', function () {

            expect($scope.alerts.length).toEqual(0);
            $scope.alert('success', 'alert text');

            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('success');
            expect($scope.alerts[0].msg).toEqual('alert text');

        });

        it('saves the user data and handles loading on success', function () {

            $httpBackend.expectPOST('/user/555', {id: 555, name: 'test'})
                .respond(200, {id: 555, name: 'test'});

            expect($scope.loading).toBe(false);
            $scope.save();
            expect($scope.loading).toBe(true);
            $httpBackend.flush();
            expect($scope.loading).toBe(false);
            expect($scope.alerts[0].type).toEqual('success');
        });

        it('saves the user data and handles loading on error', function () {

            $httpBackend.expectPOST('/user/555', {id: 555, name: 'test'})
                .respond(500, {id: 555, name: 'test'});

            expect($scope.loading).toBe(false);
            $scope.save();
            expect($scope.loading).toBe(true);
            $httpBackend.flush();
            expect($scope.loading).toBe(false);
            expect($scope.alerts[0].type).toEqual('danger');
        });

        it('reloads user on reset success', function () {

            $httpBackend.expectGET('/user/555')
                .respond(200, {id: 555, name: 'test'});

            $scope.user.name = 'boo';

            expect($scope.loading).toBe(false);
            $scope.reset();
            expect($scope.loading).toBe(true);
            $httpBackend.flush();
            expect($scope.loading).toBe(false);
            expect($scope.user.name).toEqual('test');
        });

        it('warns if fails to reload the user', function () {

            $httpBackend.expectGET('/user/555')
                .respond(500, {id: 555, name: 'test'});

            $scope.user.name = 'boo';

            expect($scope.loading).toBe(false);
            $scope.reset();
            expect($scope.loading).toBe(true);
            $httpBackend.flush();
            expect($scope.loading).toBe(false);
            expect($scope.alerts[0].type).toEqual('danger');

        });

        it('creates a new user instance when called with no id', function () {

            delete $routeParams.id;

            controller = $controller('AdminUserCtrl', {
                $scope: $scope,
                $routeParams: $routeParams,
                User: User,
                Auth: Auth
            });

            expect($scope.loading).toBe(false);
            expect($scope.create).toBe(true);
            expect($scope.user instanceof User).toBe(true);
            expect($scope.user.id).not.toBeDefined();
        });

        it('can create a new user', function () {

            delete $routeParams.id;

            controller = $controller('AdminUserCtrl', {
                $scope: $scope,
                $routeParams: $routeParams,
                User: User,
                Auth: Auth
            });

            expect($scope.loading).toBe(false);

            $scope.user.name = 'test';
            $scope.user.password = 'password';


            $httpBackend.expectPOST('/user', {name: 'test', password: 'password'})
                .respond(200, {id: 555, name: 'test'});

            $scope.createUser();

            expect($scope.loading).toBe(true);
            expect($scope.create).toBe(true);

            $httpBackend.flush();

            expect($scope.loading).toBe(false);
            expect($scope.create).toBe(false);
            expect($scope.user.id).toEqual(555);
            expect($scope.user.name).toEqual('test');

            expect($scope.alerts[0].type).toEqual('success');


        });

        it('can handle if create user fails', function () {

            delete $routeParams.id;

            controller = $controller('AdminUserCtrl', {
                $scope: $scope,
                $routeParams: $routeParams,
                User: User,
                Auth: Auth
            });

            expect($scope.loading).toBe(false);

            $scope.user.name = 'test';
            $scope.user.password = 'password';


            $httpBackend.expectPOST('/user', {name: 'test', password: 'password'})
                .respond(500, {id: 555, name: 'test'});

            $scope.createUser();

            expect($scope.loading).toBe(true);
            expect($scope.create).toBe(true);

            $httpBackend.flush();

            expect($scope.loading).toBe(false);
            expect($scope.create).toBe(true);
            expect($scope.user.id).not.toBeDefined();

            expect($scope.alerts[0].type).toEqual('danger');

        });

        it('can change password and calls $setPristine', function () {
            var pristine = null;
            $scope.forms.passwordForm = {
                $setPristine: function () {
                    pristine = true;
                }
            };

            $scope.user.password = 'pass';
            $scope.pw.newPassword = 'newPass';
            expect($scope.loading).toBe(false);


            $httpBackend.expectPOST('/auth/change-password', {credentials: $scope.user, newPassword: $scope.pw.newPassword})
                .respond(200);


            $scope.changePassword();

            expect($scope.loading).toBe(true);

            $httpBackend.flush();

            expect($scope.loading).toBe(false);
            expect(pristine).toBe(true);
            expect($scope.alerts[0].type).toEqual('success');


        });

        it('can handle if change password fails and calls $setPristine', function () {
            var pristine = null;
            $scope.forms.passwordForm = {
                $setPristine: function () {
                    pristine = true;
                }
            };

            $scope.user.password = 'pass';
            $scope.pw.newPassword = 'newPass';
            expect($scope.loading).toBe(false);


            $httpBackend.expectPOST('/auth/change-password', {credentials: $scope.user, newPassword: $scope.pw.newPassword})
                .respond(500);


            $scope.changePassword();

            expect($scope.loading).toBe(true);

            $httpBackend.flush();

            expect($scope.loading).toBe(false);
            expect(pristine).toBe(true);
            expect($scope.alerts[0].type).toEqual('danger');

        });


    });
});