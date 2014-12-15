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
        });

        it('pageChanged passes query parameters and reads X-Total', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 5;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=5&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.pageChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(2);
            expect($scope.total).toEqual(50);
        });

        it('typeChanged does nothing if query is not set', function () {

            $scope.typeChanged();

        });

        it('typeChanged calls queryChanged if query is set', function () {

            $scope.query = 'foo';
            spyOn($scope, 'queryChanged');
            $scope.typeChanged();
            expect($scope.queryChanged).toHaveBeenCalled();

        });

        it('queryChanged moves back to page 1', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 5;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=1&query=foo&size=5&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.queryChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(1);
            expect($scope.total).toEqual(50);
        });

        it('perPage queries and stays on same record', function () {
            $scope.searchBy = 'email';
            $scope.page = 2;
            $scope.query = 'foo';
            $scope._perPage = 4;
            $scope.sort = 'ASC';

            $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=4&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.pageChanged();

            $httpBackend.flush();

            expect($scope.page).toEqual(2);
            expect($scope.total).toEqual(50);

            //with 4 per page on page 2, we are on record 5
            //if we change _perPage to 2 we should end on page 3
            $httpBackend.expectGET('/user?field=email&page=3&query=foo&size=2&sort=ASC')
                .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

            $scope.perPage(2);

            $httpBackend.flush();
        });

        it('view users goes to /admin/user/:id', function () {

            $scope.viewUser(555);
            expect($location.path()).toEqual('/admin/user/555');
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