describe('AdminUserController', function () {
    var $scope, $q, $controller, $rootScope, $gapiMock, User, Auth, Endpoint, vm;
    var testUser = {id: 555, name: 'test'};

    beforeEach(module('jasifyWebTest'));

    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$q_, _$location_, _$controller_, _$rootScope_, _$routeParams_, _$gapiMock_, _jasDialogs_, _User_, _Auth_, _Endpoint_) {
        $q = _$q_;
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        $gapiMock = _$gapiMock_;
        jasDialogs = _jasDialogs_;
        User = _User_;
        Auth = _Auth_;
        Endpoint = _Endpoint_;
        Endpoint.jasifyLoaded();
    }));

    beforeEach(function () {
        $scope = $rootScope.$new();
        spyOn(User, 'get').and.returnValue($q.when(testUser));
        vm = $controller('AdminUserController', {$scope: $scope, user: testUser});
        $rootScope.$apply();
    });


    it('reads routeParams and loads user', function () {
        expect(vm.user.id).toEqual(555);
        expect(vm.user.name).toEqual('test');
    });

    it('reloads user on reset success', function () {
        User.get.and.returnValue($q.when({id: 555, name: 'test'}));
        vm.user.name = 'boo';
        vm.reset();
        $rootScope.$apply();
        expect(vm.user.name).toEqual('test');
    });

    it('creates a new user instance when called with no id', function () {

        vm = $controller('AdminUserController', {
            $scope: $scope,
            User: User,
            Auth: Auth,
            user: {}
        });

        expect(vm.user).toBeDefined();
        expect(vm.user.id).not.toBeDefined();
    });

    it('can create a new user and redirect to created user', function () {

        vm = $controller('AdminUserController', {
            $scope: $scope,
            jasDialogs: jasDialogs,
            User: User,
            Auth: Auth,
            user: {}
        });

        vm.user.name = 'test';
        vm.user.password = 'password';

        spyOn(User, 'add');
        spyOn($location, 'path');
        spyOn(jasDialogs, 'success');

        User.add.and.returnValue($q.when({id: 555, name: 'test'}));

        vm.submit();

        $rootScope.$apply();

        expect($location.path).toHaveBeenCalled();
        expect(jasDialogs.success).toHaveBeenCalled();
    });

    it('can handle if create user fails', function () {

        vm = $controller('AdminUserController', {
            $scope: $scope,
            User: User,
            Auth: Auth,
            user: {}
        });

        vm.user.name = 'test';
        vm.user.password = 'password';

        spyOn(User, 'add');
        User.add.and.returnValue($q.reject({id: 555, name: 'test'}));

        vm.submit();

        $rootScope.$apply();

        expect(vm.user.id).not.toBeDefined();
    });

    it('can change password and calls $setPristine', function () {
        var pristine = null,
            untouched = null;

        vm.forms.passwordForm = {
            $setPristine: function () {
                pristine = true;
            },
            $setUntouched: function () {
                untouched = true;
            }
        };

        vm.user.password = 'pass';
        vm.pw.newPassword = 'newPass';

        vm.changePassword();

        $rootScope.$apply();

        expect(pristine).toBe(true);
    });

    it('can handle if change password fails and calls $setPristine', function () {
        var pristine = null,
            untouched = null;

        vm.forms.passwordForm = {
            $setPristine: function () {
                pristine = true;
            },
            $setUntouched: function () {
                untouched = true;
            }
        };

        vm.user.password = 'pass';
        vm.pw.newPassword = 'newPass';

        $gapiMock.client.jasify.auth.changePassword = function () {
            return $q.reject();
        };

        vm.changePassword();

        $rootScope.$apply();

        expect(pristine).toBe(true);
        expect(untouched).toBe(true);
    });


});
