describe('LoginController', function () {
    var $scope, controller, $rootScope, $applicationScope, $q, $httpBackend, Auth, AUTH_EVENTS;
    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$q_, _$httpBackend_, $controller, _$rootScope_, _Auth_, _AUTH_EVENTS_) {
        $q = _$q_;
        $httpBackend = _$httpBackend_;
        Auth = _Auth_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        $rootScope = _$rootScope_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {$scope: $applicationScope});

        $scope = $applicationScope.$new();
        controller = $controller('LoginController', {$scope: $scope});
    }));

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('sets current user on successful login', function () {
        $scope.credentials = {name: 'test', password: 'password'};
        var user = {name: $scope.credentials.name, id: 555};
        var defer = $q.defer();
        defer.resolve(user);
        spyOn(Auth, 'login').and.returnValue(defer.promise);

        $scope.login($scope.credentials);

        expect($scope.currentUser).toEqual(null);

        $rootScope.$apply();

        expect($scope.currentUser).toEqual(user);
        expect(Auth.login).toHaveBeenCalledWith($scope.credentials);
    });

    it('broadcasts on successful login', function () {
        $scope.credentials = {name: 'test', password: 'password'};
        var user = {name: $scope.credentials.name, id: 555};
        var defer = $q.defer();
        defer.resolve(user);
        spyOn(Auth, 'login').and.returnValue(defer.promise);
        spyOn($rootScope, '$broadcast').and.callThrough();

        $scope.login($scope.credentials);


        $rootScope.$apply();

        expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess);

    });

    it('broadcasts on failed login', function () {
        $scope.credentials = {name: 'test', password: 'password'};
        var defer = $q.defer();
        defer.reject();
        spyOn(Auth, 'login').and.returnValue(defer.promise);

        $scope.login($scope.credentials);
        spyOn($rootScope, '$broadcast').and.callThrough();

        $rootScope.$apply();

        expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginFailed);

    });
});
