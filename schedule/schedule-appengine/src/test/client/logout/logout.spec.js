describe('LogoutController', function () {
    var $scope, vm, Session, $q, $applicationScope, Auth, $rootScope, AUTH_EVENTS, $window = {};

    beforeEach(module('jasifyWebTest'));

    beforeEach(inject(function (_Session_, _$q_, _$rootScope_, $controller, _Auth_, _AUTH_EVENTS_) {
        Session = _Session_;
        $q = _$q_;
        Auth = _Auth_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        $rootScope = _$rootScope_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {
            $scope: $applicationScope,
            $window: $window
        });
        $scope = $applicationScope.$new();

        vm = $controller('LogoutController', {
            $scope: $scope,
            $window: $window
        });
    }));

    it('can logout', function () {

        var defer = $q.defer();
        defer.resolve();
        spyOn(Auth, 'logout').and.returnValue(defer.promise);
        spyOn($rootScope, '$broadcast').and.callThrough();
        Session.create("aa", 555, false);

        vm.logout();

        expect($rootScope.$broadcast).not.toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess);

        $rootScope.$apply();

        expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess);
        expect($window.location).toBe("login.html");
    });

});
