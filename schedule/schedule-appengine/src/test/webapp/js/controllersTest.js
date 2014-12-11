describe('Controllers', function () {

    var $controller;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
    }));

    beforeEach(inject(function (_$controller_) {
        $controller = _$controller_;
    }));

    describe('ApplicationCtrl', function () {
        var $scope, controller;

        beforeEach(function () {
            $scope = {};
            controller = $controller('ApplicationCtrl', {$scope: $scope});
        });

        it('keeps a reference to the current user', function () {

            expect($scope.currentUser).toBe(null);

            var u = {test: 'test'};

            $scope.setCurrentUser(u);

            expect($scope.currentUser).toEqual(u);

        });
    });

    describe('NavbarCtrl', function () {
        var $scope, controller, $rootScope, $location, Auth, AUTH_EVENTS;

        beforeEach(inject(function (_$rootScope_, _$location_, _Auth_, _AUTH_EVENTS_) {
            $rootScope = _$rootScope_;
            $location = _$location_;
            Auth = _Auth_;
            AUTH_EVENTS = _AUTH_EVENTS_;
        }));

        beforeEach(function () {
            $scope = $rootScope.$new();
            controller = $controller('NavbarCtrl', {
                $scope: $scope,
                $location: $location,
                Auth: Auth,
                AUTH_EVENTS: AUTH_EVENTS
            });
        });

        it('starts with a collapsed navbar ', function () {
            expect($scope.navbarCollapsed).toBe(true);

        });

        it('can toggle the collapse status of the navbar ', function () {

            expect($scope.navbarCollapsed).toBe(true);
            $scope.toggleCollapse();
            expect($scope.navbarCollapsed).toBe(false);
            $scope.toggleCollapse();
            expect($scope.navbarCollapsed).toBe(true);

        });

        it('can collapse the navbar ', function () {

            expect($scope.navbarCollapsed).toBe(true);
            $scope.toggleCollapse();
            expect($scope.navbarCollapsed).toBe(false);
            $scope.collapse();
            expect($scope.navbarCollapsed).toBe(true);

        });

        it('can determine if a menu is active ', function () {

            expect($scope.menuActive('/profile')).toBe(false);

            $location.path('/profile');

            expect($scope.menuActive('/profile')).toBe('active');

        });

        it('should register a loginSucceeded as a listener for the login event', function () {

            $scope = $rootScope.$new();
            spyOn($scope, '$on');
            controller = $controller('NavbarCtrl', {
                $scope: $scope,
                $location: $location,
                Auth: Auth,
                AUTH_EVENTS: AUTH_EVENTS
            });
            expect($scope.$on).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess, $scope.loginSucceeded);

        });

        it('should watch the $location.path', function () {
            $location.path('/tmp');
            $rootScope.$digest();
            expect($scope.path).toEqual('/tmp');
            $location.path('/tmp2');
            expect($scope.path).toEqual('/tmp');
            $rootScope.$digest();
            expect($scope.path).toEqual('/tmp2');
        });
    });

});