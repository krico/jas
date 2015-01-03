describe('ProfileController', function () {
    var $scope, $httpBackend, $applicationScope, vm, $routeParams, User, Session, $controller, $rootScope;

    beforeEach(module('jasify'));

    beforeEach(inject(function (_$routeParams_, _$httpBackend_, _User_, _Session_, _$controller_, _$rootScope_) {
        $routeParams = _$routeParams_;
        $httpBackend = _$httpBackend_;
        $controller = _$controller_;
        User = _User_;
        Session = _Session_;
        $rootScope = _$rootScope_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {$scope: $applicationScope});
    }));

    var construct = function () {
        Session.create(1, 555);
        $httpBackend
            .expectGET('/user/555')
            .respond(200, {id: 555, name: 'test'});

        vm = $controller('ProfileController', {
            $scope: $scope,
            $routeParams: $routeParams,
            Session: Session,
            User: User
        });
    };

    beforeEach(function () {
        $scope = $applicationScope.$new();
        construct();
    });

    it('knows if isWelcome is tru or note', function () {
        $httpBackend.flush(); //load the user

        $routeParams.extra = 'welcome';
        construct();
        $httpBackend.flush(); //load the user
        expect($scope.isWelcome()).toEqual(true);

        $routeParams.extra = 'foo';
        construct();
        $httpBackend.flush(); //load the user
        expect($scope.isWelcome()).toEqual(false);
    });

    it('sets extra to false when there are not route parameters', function () {
        $httpBackend.flush(); //load the user
        expect($scope.isWelcome()).toEqual(false);
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

    it('saves the user and updates currentUser and calls setPristine', function () {
        $httpBackend.flush(); //load the user

        $scope.user.about = 'about him';

        $httpBackend
            .expectPOST('/user/555')
            .respond(200, $scope.user);

        var called = null;

        $scope.profileForm = {
            $setPristine: function () {
                called = true;
            }
        };


        $scope.save();

        $httpBackend.flush();

        expect($scope.currentUser.about).toEqual('about him');
        expect(called).toBe(true);
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

    it('calls profileForm.$setPristine on reset ', function () {
        $httpBackend.flush(); //load the user

        $httpBackend
            .expectGET('/user/555')
            .respond(200, {id: 555, name: 'test'});

        var called = null;
        $scope.profileForm = {
            $setPristine: function () {
                called = true;
            }
        };

        $scope.reset();

        $httpBackend.flush();

        expect(called).toEqual(true);

    });

});
