describe('AdminUsersController', function () {
    var $controller, $httpBackend, $location, User, vm;

    beforeEach(module('jasify', function ($provide) {
        $gapiMock = jasifyGapiMock();
        $provide.value('$gapi', $gapiMock);
    }));


    beforeEach(inject(function (_$controller_, _$httpBackend_, _$location_, _User_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $location = _$location_;
        User = _User_;
    }));

    beforeEach(function () {
        $httpBackend.expectGET('/user?field=name&page=1&query=&size=10&sort=DESC')
            .respond(200);
        vm = $controller('AdminUsersController', {$location: $location, User: User});
        $httpBackend.flush();
    });

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });


    it('can be instantiated', function () {
    });

    it('pageChanged passes query parameters and reads X-Total', function () {
        vm.searchBy = 'email';
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 5;
        vm.sort = 'ASC';

        $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=5&sort=ASC')
            .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

        vm.pageChanged();

        $httpBackend.flush();

        expect(vm.page).toEqual(2);
        expect(vm.total).toEqual(50);
    });

    it('typeChanged does nothing if query is not set', function () {

        vm.typeChanged();

    });

    it('typeChanged calls queryChanged if query is set', function () {

        vm.query = 'foo';
        spyOn(vm, 'queryChanged');
        vm.typeChanged();
        expect(vm.queryChanged).toHaveBeenCalled();

    });

    it('queryChanged moves back to page 1', function () {
        vm.searchBy = 'email';
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 5;
        vm.sort = 'ASC';

        $httpBackend.expectGET('/user?field=email&page=1&query=foo&size=5&sort=ASC')
            .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

        vm.queryChanged();

        $httpBackend.flush();

        expect(vm.page).toEqual(1);
        expect(vm.total).toEqual(50);
    });

    it('perPage queries and stays on same record', function () {
        vm.searchBy = 'email';
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 4;
        vm.sort = 'ASC';

        $httpBackend.expectGET('/user?field=email&page=2&query=foo&size=4&sort=ASC')
            .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

        vm.pageChanged();

        $httpBackend.flush();

        expect(vm.page).toEqual(2);
        expect(vm.total).toEqual(50);

        //with 4 per page on page 2, we are on record 5
        //if we change _perPage to 2 we should end on page 3
        $httpBackend.expectGET('/user?field=email&page=3&query=foo&size=2&sort=ASC')
            .respond(200, [{id: 10}, {id: 11}, {id: 12}, {id: 13}, {id: 14}, {id: 15}], {'X-Total': 50});

        vm.perPage(2);

        $httpBackend.flush();
    });

    it('view users goes to /admin/user/:id', function () {

        vm.viewUser(555);
        expect($location.path()).toEqual('/admin/user/555');
    });

});

