describe('AdminUserController', function () {
    var $controller, $rootScope, $httpBackend, $routeParams, $q, $gapiMock, User, Auth, Endpoint, vm;

    beforeEach(module('jasify'));

    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$controller_, _$rootScope_, _$httpBackend_, _$routeParams_, _$q_, _$gapiMock_, _User_, _Auth_, _Endpoint_) {
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        $routeParams = _$routeParams_;
        $q = _$q_;
        $gapiMock = _$gapiMock_;
        User = _User_;
        Auth = _Auth_;
        Endpoint = _Endpoint_;
        Endpoint.jasifyLoaded();
        $gapiMock.client.jasify.user = {};
    }));

    beforeEach(function () {
        $scope = $rootScope.$new();
        $httpBackend.expectGET('/user/555')
            .respond(200, {id: 555, name: 'test'});

        $routeParams.id = 555;

        vm = $controller('AdminUserController', {
            $scope: $scope,
            $routeParams: $routeParams,
            User: User,
            Auth: Auth
        });

        $httpBackend.flush();

    });

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });


    it('reads routeParams and loads user', function () {

        expect(vm.create).toBe(false);

        expect(vm.user.id).toEqual(555);
        expect(vm.user.name).toEqual('test');

    });

    it('can handle alerts', function () {

        expect(vm.alerts.length).toEqual(0);
        vm.alert('success', 'alert text');

        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('success');
        expect(vm.alerts[0].msg).toEqual('alert text');

    });

    it('saves the user data and handles loading on success', function () {

        $httpBackend.expectPOST('/user/555', {id: 555, name: 'test'})
            .respond(200, {id: 555, name: 'test'});

        expect(vm.loading).toBe(false);
        vm.save();
        expect(vm.loading).toBe(true);
        $httpBackend.flush();
        expect(vm.loading).toBe(false);
        expect(vm.alerts[0].type).toEqual('success');
    });

    it('saves the user data and handles loading on error', function () {

        $httpBackend.expectPOST('/user/555', {id: 555, name: 'test'})
            .respond(500, {id: 555, name: 'test'});

        expect(vm.loading).toBe(false);
        vm.save();
        expect(vm.loading).toBe(true);
        $httpBackend.flush();
        expect(vm.loading).toBe(false);
        expect(vm.alerts[0].type).toEqual('danger');
    });

    it('reloads user on reset success', function () {

        $httpBackend.expectGET('/user/555')
            .respond(200, {id: 555, name: 'test'});

        vm.user.name = 'boo';

        expect(vm.loading).toBe(false);
        vm.reset();
        expect(vm.loading).toBe(true);
        $httpBackend.flush();
        expect(vm.loading).toBe(false);
        expect(vm.user.name).toEqual('test');
    });

    it('warns if fails to reload the user', function () {

        $httpBackend.expectGET('/user/555')
            .respond(500, {id: 555, name: 'test'});

        vm.user.name = 'boo';

        expect(vm.loading).toBe(false);
        vm.reset();
        expect(vm.loading).toBe(true);
        $httpBackend.flush();
        expect(vm.loading).toBe(false);
        expect(vm.alerts[0].type).toEqual('danger');

    });

    it('creates a new user instance when called with no id', function () {

        delete $routeParams.id;

        vm = $controller('AdminUserController', {
            $scope: $scope,
            $routeParams: $routeParams,
            User: User,
            Auth: Auth
        });

        expect(vm.loading).toBe(false);
        expect(vm.create).toBe(true);
        expect(vm.user instanceof User).toBe(true);
        expect(vm.user.id).not.toBeDefined();
    });

    it('can create a new user', function () {

        delete $routeParams.id;

        vm = $controller('AdminUserController', {
            $scope: $scope,
            $routeParams: $routeParams,
            User: User,
            Auth: Auth
        });

        expect(vm.loading).toBe(false);

        vm.user.name = 'test';
        vm.user.password = 'password';


        $httpBackend.expectPOST('/user', {name: 'test', password: 'password'})
            .respond(200, {id: 555, name: 'test'});

        vm.createUser();

        expect(vm.loading).toBe(true);
        expect(vm.create).toBe(true);

        $httpBackend.flush();

        expect(vm.loading).toBe(false);
        expect(vm.create).toBe(false);
        expect(vm.user.id).toEqual(555);
        expect(vm.user.name).toEqual('test');

        expect(vm.alerts[0].type).toEqual('success');


    });

    it('can handle if create user fails', function () {

        delete $routeParams.id;

        vm = $controller('AdminUserController', {
            $scope: $scope,
            $routeParams: $routeParams,
            User: User,
            Auth: Auth
        });

        expect(vm.loading).toBe(false);

        vm.user.name = 'test';
        vm.user.password = 'password';


        $httpBackend.expectPOST('/user', {name: 'test', password: 'password'})
            .respond(500, {id: 555, name: 'test'});

        vm.createUser();

        expect(vm.loading).toBe(true);
        expect(vm.create).toBe(true);

        $httpBackend.flush();

        expect(vm.loading).toBe(false);
        expect(vm.create).toBe(true);
        expect(vm.user.id).not.toBeDefined();

        expect(vm.alerts[0].type).toEqual('danger');

    });

    it('can change password and calls $setPristine', function () {
        var pristine = null;
        vm.forms.passwordForm = {
            $setPristine: function () {
                pristine = true;
            }
        };

        vm.user.password = 'pass';
        vm.pw.newPassword = 'newPass';
        expect(vm.loading).toBe(false);

        vm.changePassword();

        expect(vm.loading).toBe(true);


        $rootScope.$apply();

        expect(vm.loading).toBe(false);
        expect(pristine).toBe(true);
        expect(vm.alerts[0].type).toEqual('success');


    });

    it('can handle if change password fails and calls $setPristine', function () {
        var pristine = null;
        vm.forms.passwordForm = {
            $setPristine: function () {
                pristine = true;
            }
        };

        vm.user.password = 'pass';
        vm.pw.newPassword = 'newPass';
        expect(vm.loading).toBe(false);

        $gapiMock.client.jasify.auth.changePassword = function () {
            return $q.reject();
        };

        vm.changePassword();

        expect(vm.loading).toBe(true);

        $rootScope.$apply();

        expect(vm.loading).toBe(false);
        expect(pristine).toBe(true);
        expect(vm.alerts[0].type).toEqual('danger');

    });


});
