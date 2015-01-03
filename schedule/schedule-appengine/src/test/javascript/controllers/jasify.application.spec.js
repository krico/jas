describe('ApplicationController', function () {
    var $controller, $rootScope, $location, $modalMock, $scope, vm, AUTH_EVENTS;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$controller_, _$rootScope_, _$location_, _$modalMock_, _AUTH_EVENTS_) {
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        $modalMock = _$modalMock_;
        AUTH_EVENTS = _AUTH_EVENTS_;

        $scope = $rootScope.$new();
        vm = $controller('ApplicationController', {$scope: $scope});
    }));

    it('keeps a reference to the current user', function () {

        expect($scope.currentUser).toBe(null);

        var u = {test: 'test'};

        $scope.setCurrentUser(u);

        expect($scope.currentUser).toEqual(u);

    });

    it('can determine if a menu is active ', function () {

        expect($scope.menuActive('/profile')).toBe(false);

        $location.path('/profile');

        expect($scope.menuActive('/profile')).toBe('active');

    });

    it('reacts on AUTH_EVENT.notAuthorized', function () {
        $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
        $rootScope.$apply();
        expect($modalMock.data.open.confirmCallback).toBeDefined();
        expect($modalMock.data.open.cancelCallback).toBeDefined();
    });

    it('reacts on AUTH_EVENT.notAuthorized', function () {
        $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
        $rootScope.$apply();
        expect($modalMock.data.open.confirmCallback).toBeDefined();
        expect($modalMock.data.open.cancelCallback).toBeDefined();
    });
});
