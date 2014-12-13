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

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

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

        it('should redirect /login to /profile on loginSucceeded', function () {

            $location.path('/login');
            $scope.loginSucceeded();
            expect($location.path()).toEqual('/profile');

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

    describe('SignUpCtrl', function () {
        var $scope, controller, AUTH_EVENTS, $applicationScope, Auth, User;

        beforeEach(inject(function (_$location_, _AUTH_EVENTS_, _Auth_, _User_) {
            AUTH_EVENTS = _AUTH_EVENTS_;
            Auth = _Auth_;
            User = _User_;
        }));

        beforeEach(function () {
            $applicationScope = $rootScope.$new();
            $scope = $applicationScope.$new();

            //to create the scope tree, we instantiate applicationCtrl
            $controller('ApplicationCtrl', {$scope: $applicationScope});

            controller = $controller('SignUpCtrl', {
                $scope: $scope,
                $rootScope: $rootScope,
                Auth: Auth,
                User: User
            });
        });

        it('can handle alerts', function () {

            expect($scope.alerts.length).toEqual(0);
            $scope.alert('success', 'alert text');

            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('success');
            expect($scope.alerts[0].msg).toEqual('alert text');

        });

        it('can test if a form field for success and error', function () {
            var form = {'someField': {}};
            $scope.signUpForm = form;
            expect($scope.hasError('someField')).not.toBe(true);
            expect($scope.hasSuccess('someField')).not.toBe(true);

            // not dirty, so still not error or success
            form.someField.$invalid = true;
            form.someField.$valid = false;
            expect($scope.hasError('someField')).not.toBe(true);
            expect($scope.hasSuccess('someField')).not.toBe(true);

            // not dirty, so still not error or success
            form.someField.$invalid = false;
            form.someField.$valid = true;
            expect($scope.hasError('someField')).not.toBe(true);
            expect($scope.hasSuccess('someField')).not.toBe(true);

            form.someField.$dirty = true;
            expect($scope.hasError('someField')).not.toBe(true);
            expect($scope.hasSuccess('someField')).toBe(true);

            form.someField.$invalid = true;
            form.someField.$valid = false;
            expect($scope.hasError('someField')).toBe(true);
            expect($scope.hasSuccess('someField')).not.toBe(true);
        });

        it('can register a new user', function () {
            $scope.user = {name: 'user', password: 'password'};

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(false);

            $httpBackend
                .expectPOST('/user', $scope.user)
                .respond(200);

            //after save
            $httpBackend
                .expectPOST('/auth/login', $scope.user)
                .respond(200, {id: 'someSessionId', userId: 555, user: {id: 555, name: $scope.user.name}});

            $scope.createUser();

            //check the async nature of inProgress
            expect($scope.inProgress).toBe(true);
            expect($scope.registered).toBe(false);
            expect($scope.alerts.length).toEqual(0);

            $httpBackend.flush(1);

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(true);
            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('success');

            spyOn($rootScope, '$broadcast');
            $httpBackend.flush();

            expect($scope.alerts.length).toEqual(1);
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess);

            expect($scope.currentUser).not.toEqual(null);
            expect($scope.currentUser.name).toEqual('user');
            expect($scope.currentUser.id).toEqual(555);

        });

        it('can handle a failed registration', function () {
            $scope.user = {name: 'user', password: 'password'};

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(false);

            $httpBackend
                .expectPOST('/user', $scope.user)
                .respond(500);

            $scope.createUser();

            //check the async nature of inProgress
            expect($scope.inProgress).toBe(true);
            expect($scope.registered).toBe(false);
            expect($scope.alerts.length).toEqual(0);

            $httpBackend.flush();

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(false);
            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('danger');
            expect($scope.currentUser).toEqual(null);


        });

        it('can handle a failed login after a successful registration', function () {
            $scope.user = {name: 'user', password: 'password'};

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(false);

            $httpBackend
                .expectPOST('/user', $scope.user)
                .respond(200);

            //after save
            $httpBackend
                .expectPOST('/auth/login', $scope.user)
                .respond(401);

            $scope.createUser();

            //check the async nature of inProgress
            expect($scope.inProgress).toBe(true);
            expect($scope.registered).toBe(false);
            expect($scope.alerts.length).toEqual(0);

            $httpBackend.flush(1);

            expect($scope.inProgress).toBe(false);
            expect($scope.registered).toBe(true);
            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('success');

            spyOn($rootScope, '$broadcast');
            $httpBackend.flush();

            expect($scope.alerts.length).toEqual(2);
            expect($scope.alerts[1].type).toEqual('danger');
            expect($rootScope.$broadcast).not.toHaveBeenCalled();
            expect($scope.currentUser).toEqual(null);
        });
    });

    describe('LogoutCtrl', function () {
        var $scope, controller, AUTH_EVENTS, Auth, Session;

        beforeEach(inject(function (_AUTH_EVENTS_, _Auth_, _Session_) {
            AUTH_EVENTS = _AUTH_EVENTS_;
            Auth = _Auth_;
            Session = _Session_;
        }));

        beforeEach(function () {
            $applicationScope = $rootScope.$new();
            $scope = $applicationScope.$new();

            //to create the scope tree, we instantiate applicationCtrl
            $controller('ApplicationCtrl', {$scope: $applicationScope});

            controller = $controller('LogoutCtrl', {
                $scope: $scope,
                $rootScope: $rootScope,
                Auth: Auth,
                AUTH_EVENTS: AUTH_EVENTS
            });
        });

        it('can logout', function () {

            $scope.setCurrentUser({id: 15});
            Session.create(1, 2);

            $httpBackend
                .expectGET('/auth/logout')
                .respond(200);
            $scope.logout();

            spyOn($rootScope, '$broadcast');
            $httpBackend.flush();
            expect($rootScope.$broadcast).toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess);
            expect(Auth.isAuthenticated()).toBe(false);
            expect($scope.currentUser).toBe(null);
        });

    });

    describe('ProfileCtrl', function () {
        var $scope, controller, User, Session;

        beforeEach(inject(function (_User_, _Session_) {
            User = _User_;
            Session = _Session_;
        }));

        beforeEach(function () {
            $applicationScope = $rootScope.$new();
            $scope = $applicationScope.$new();

            //to create the scope tree, we instantiate applicationCtrl
            $controller('ApplicationCtrl', {$scope: $applicationScope});

            Session.create(1, 555);
            $httpBackend
                .expectGET('/user/555')
                .respond(200, {id: 555, name: 'test'});

            controller = $controller('ProfileCtrl', {
                $scope: $scope,
                Session: Session,
                User: User
            });
        });

        it('can handle alerts', function () {
            $httpBackend.flush(); //load the user

            expect($scope.alerts.length).toEqual(0);
            $scope.alert('success', 'alert text');

            expect($scope.alerts.length).toEqual(1);
            expect($scope.alerts[0].type).toEqual('success');
            expect($scope.alerts[0].msg).toEqual('alert text');

        });

        it('loads user when constructed', function () {
            expect($scope.user.$resolved).toEqual(false);
            $httpBackend.flush(); //load the user
            expect($scope.user.$resolved).toEqual(true);
            expect($scope.user).toBeDefined();
            expect($scope.user.name).toEqual('test');
        });

        it('saves the user and updates currentUser ', function () {
            $httpBackend.flush(); //load the user

            $scope.user.about = 'about him';

            $httpBackend
                .expectPOST('/user/555')
                .respond(200, $scope.user);

            $scope.save();

            $httpBackend.flush();

            expect($scope.currentUser.about).toEqual('about him');
        });

        it('resets to original user ', function () {
            $httpBackend.flush(); //load the user

            $scope.user.about = 'about him';

            $httpBackend
                .expectGET('/user/555')
                .respond(200, {id: 555, name: 'test'});

            $scope.reset();

            $httpBackend.flush();

            expect($scope.user.about).not.toBeDefined();
        });

    });


});