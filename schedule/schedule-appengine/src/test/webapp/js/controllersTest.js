describe('Controllers', function () {

    var $controller, $httpBackend, $rootScope;

    beforeEach(module('jasifyScheduleApp', function ($provide) {
        $provide.value('$log', console);
    }));

    beforeEach(inject(function (_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
    }));

    describe('ApplicationCtrl', function () {
        var $scope, controller;

        beforeEach(function () {
            $scope = $rootScope.$new();
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
        var $scope, controller, $location, Auth, AUTH_EVENTS;

        beforeEach(inject(function (_$location_, _Auth_, _AUTH_EVENTS_) {
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

    describe('HomeCtrl', function () {
        var $scope, controller;

        beforeEach(function () {
            $scope = $rootScope.$new();
            controller = $controller('HomeCtrl', {$scope: $scope});
        });

        it('is empty', function () {
        });

    });

    describe('LoginCtrl', function () {
        var $scope, controller, $applicationScope, Auth, AUTH_EVENTS;

        beforeEach(inject(function (_Auth_, _AUTH_EVENTS_) {
            Auth = _Auth_;
            AUTH_EVENTS = _AUTH_EVENTS_;
        }));

        beforeEach(function () {
            $applicationScope = $rootScope.$new();
            $scope = $applicationScope.$new();

            //to create the scope tree, we instantiate applicationCtrl
            $controller('ApplicationCtrl', {$scope: $applicationScope});

            controller = $controller('LoginCtrl', {
                $scope: $scope,
                $rootScope: $rootScope,
                Auth: Auth,
                AUTH_EVENTS: AUTH_EVENTS
            });
        });

        it('sets current user on successful login', function () {
            $scope.credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', $scope.credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: $scope.credentials.name}});

            $scope.login($scope.credentials);

            expect($scope.currentUser).toEqual(null);

            $httpBackend.flush();

            expect($scope.currentUser).not.toEqual(null);
            expect($scope.currentUser.name).toEqual('test');
            expect($scope.currentUser.id).toEqual(555);

        });

        it('broadcasts on successful login', function () {
            $scope.credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', $scope.credentials)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: $scope.credentials.name}});

            $scope.login($scope.credentials);

            spyOn($rootScope, '$broadcast');

            $httpBackend.flush();

            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess);

        });

        it('broadcasts on failed login', function () {
            $scope.credentials = {name: 'test', password: 'password'};
            $httpBackend
                .expectPOST('/auth/login', $scope.credentials)
                .respond(401);

            $scope.login($scope.credentials);
            spyOn($rootScope, '$broadcast');

            $httpBackend.flush();

            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginFailed);

        });
    });


});