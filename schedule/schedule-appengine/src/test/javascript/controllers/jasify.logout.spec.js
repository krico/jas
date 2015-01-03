describe('LogoutController', function () {
    var $scope, vm, Session, $q, $applicationScope, Auth, $rootScope, AUTH_EVENTS;

    beforeEach(module('jasify'));

    beforeEach(inject(function (_Session_, _$q_, _$rootScope_, $controller, _Auth_, _AUTH_EVENTS_) {
        Session = _Session_;
        $q = _$q_;
        Auth = _Auth_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        $rootScope = _$rootScope_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {$scope: $applicationScope});
        $scope = $applicationScope.$new();

        vm = $controller('LogoutController', {
            $scope: $scope
        });
    }));

    it('can logout', function () {
        $scope.setCurrentUser({});
        var defer = $q.defer();
        defer.resolve();
        spyOn(Auth, 'logout').and.returnValue(defer.promise);
        spyOn($rootScope, '$broadcast').and.callThrough();
        Session.create("aa", 555, false);

        $scope.logout();

        expect($rootScope.$broadcast).not.toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess);
        expect($scope.currentUser).not.toBe(null);

        $rootScope.$apply();

        expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess);
        expect($scope.currentUser).toBe(null);
    });

});
