describe('SignUpController', function () {
    var $httpBackend, $scope, $rootScope, $applicationScope, vm, User, AUTH_EVENTS;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$httpBackend_, _$rootScope_, _User_, $controller, _AUTH_EVENTS_) {
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        User = _User_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {$scope: $applicationScope});
        $scope = $applicationScope.$new();

        vm = $controller('SignUpController', {
            $scope: $scope
        });
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('can handle alerts', function () {

        expect(vm.alerts.length).toEqual(0);
        vm.alert('success', 'alert text');

        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('success');
        expect(vm.alerts[0].msg).toEqual('alert text');

    });

    it('can test if a form field for success and error', function () {
        var form = {'someField': {}};
        vm.signUpForm = form;
        expect(vm.hasError('someField')).not.toBe(true);
        expect(vm.hasSuccess('someField')).not.toBe(true);

        // not dirty, so still not error or success
        form.someField.$invalid = true;
        form.someField.$valid = false;
        expect(vm.hasError('someField')).not.toBe(true);
        expect(vm.hasSuccess('someField')).not.toBe(true);

        // not dirty, so still not error or success
        form.someField.$invalid = false;
        form.someField.$valid = true;
        expect(vm.hasError('someField')).not.toBe(true);
        expect(vm.hasSuccess('someField')).not.toBe(true);

        form.someField.$dirty = true;
        expect(vm.hasError('someField')).not.toBe(true);
        expect(vm.hasSuccess('someField')).toBe(true);

        form.someField.$invalid = true;
        form.someField.$valid = false;
        expect(vm.hasError('someField')).toBe(true);
        expect(vm.hasSuccess('someField')).not.toBe(true);
    });

    it('can register a new user', function () {
        vm.user = {name: 'user', password: 'password'};

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(false);

        $httpBackend
            .expectPOST('/user', vm.user)
            .respond(200);

        //after save
        $httpBackend
            .expectGET('/auth/restore')
            .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: vm.user.name}});

        vm.createUser();

        //check the async nature of inProgress
        expect(vm.inProgress).toBe(true);
        expect(vm.registered).toBe(false);
        expect(vm.alerts.length).toEqual(0);

        $httpBackend.flush(1);

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(true);
        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('success');

        spyOn($rootScope, '$broadcast');
        $httpBackend.flush();

        expect(vm.alerts.length).toEqual(1);
        expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess);

        expect($scope.currentUser).not.toEqual(null);
        expect($scope.currentUser.name).toEqual('user');
        expect($scope.currentUser.id).toEqual(555);

    });

    it('can handle a failed registration', function () {
        vm.user = {name: 'user', password: 'password'};

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(false);

        $httpBackend
            .expectPOST('/user', vm.user)
            .respond(500);

        vm.createUser();

        //check the async nature of inProgress
        expect(vm.inProgress).toBe(true);
        expect(vm.registered).toBe(false);
        expect(vm.alerts.length).toEqual(0);

        $httpBackend.flush();

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(false);
        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('danger');
        expect($scope.currentUser).toEqual(null);


    });

    it('can handle a failed login after a successful registration', function () {
        vm.user = {name: 'user', password: 'password'};

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(false);

        $httpBackend
            .expectPOST('/user', vm.user)
            .respond(200);

        //after save
        $httpBackend
            .expectGET('/auth/restore')
            .respond(401);

        vm.createUser();

        //check the async nature of inProgress
        expect(vm.inProgress).toBe(true);
        expect(vm.registered).toBe(false);
        expect(vm.alerts.length).toEqual(0);

        $httpBackend.flush(1);

        expect(vm.inProgress).toBe(false);
        expect(vm.registered).toBe(true);
        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('success');

        spyOn($rootScope, '$broadcast');
        $httpBackend.flush();

        expect(vm.alerts.length).toEqual(2);
        expect(vm.alerts[1].type).toEqual('danger');
        expect($rootScope.$broadcast).not.toHaveBeenCalled();
        expect($scope.currentUser).toEqual(null);
    });
});
