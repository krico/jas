describe('AdminControllers', function () {

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

    describe('AdminUsersCtrl', function () {
        var $scope, controller, $location, User;

        beforeEach(inject(function (_$location_, _User_) {
            $location = _$location_;
            User = _User_;
        }));

        beforeEach(function () {
            $scope = $rootScope.$new();
            $httpBackend.expectGET('/user?field=name&page=1&query=&size=10&sort=DESC')
                .respond(200);
            controller = $controller('AdminUsersCtrl', {$scope: $scope, $location: $location, User: User});
            $httpBackend.flush();
        });

        it('can be instantiated', function () {
            //TODO: really test
        });
    });

    describe('AdminUserCtrl', function () {
        var $scope, controller, $routeParams, $modal, User, Auth;

        beforeEach(inject(function (_$routeParams_, _$modal_, _User_, _Auth_) {
            $routeParams = _$routeParams_;
            $modal = _$modal_;
            User = _User_;
            Auth = _Auth_;
        }));

        beforeEach(function () {
            $scope = $rootScope.$new();
            $httpBackend.expectGET('/user/555')
                .respond(200, {id: 555, name: 'test'});

            $routeParams.id = 555;

            controller = $controller('AdminUserCtrl', {
                $scope: $scope,
                $routeParams: $routeParams,
                User: User,
                Auth: Auth
            });

            $httpBackend.flush();

        });

        it('can be instantiated', function () {
            //TODO: really test
        });
    });
});