describe('AdminUsersController', function () {
    var $controller, $location, User, vm, $q, $rootScope;

    beforeEach(module('jasifyWebTest'));


    beforeEach(inject(function (_$controller_, _$location_, _User_, _$q_, _$rootScope_) {
        $controller = _$controller_;
        $location = _$location_;
        User = _User_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    beforeEach(function () {
        spyOn(User, 'query').and.returnValue($q.when({total: 0, users: []}));
        vm = $controller('AdminUsersController', {$location: $location, User: User});
        $rootScope.$apply();
    });


    it('can be instantiated', function () {
    });

    it('pageChanged passes query parameters and sets total to 0 if no results', function () {
        vm.searchBy('email');
        vm.pagination.page = 2;
        vm.query = 'foo';
        vm._perPage = 5;
        vm.sort = 'ASC';

        User.query.and.returnValue($q.when({total: 50, users: []}));

        vm.pageChanged();

        $rootScope.$apply();

        expect(vm.pagination.page).toEqual(2);
        expect(vm.pagination.total).toEqual(0);
        expect(User.query).toHaveBeenCalledWith({
            field: vm.searchBy(),
            offset: ((vm.pagination.page - 1) * vm.pagination.itemsPerPage),
            limit: vm.pagination.itemsPerPage,
            sort: vm.sort,
            query: vm.query
        });
    });

    it('searchByChanged does nothing if query is not set', function () {

        vm.searchByChanged();

    });

    it('searchByChanged calls queryChanged if query is set', function () {

        vm.query = 'foo';
        spyOn(vm, 'queryChanged');
        vm.searchByChanged();
        expect(vm.queryChanged).toHaveBeenCalled();

    });

    it('queryChanged moves back to page 1 and set total to row count', function () {
        vm.searchBy('email');
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 5;
        vm.sort = 'ASC';

        User.query.and.returnValue($q.when({total: 50, users: [{}, {}]}));

        vm.queryChanged();

        $rootScope.$apply();

        expect(vm.pagination.page).toEqual(1);
        expect(vm.pagination.total).toEqual(2);
    });

    it('perPage queries and stays on same record', function () {
        vm.searchBy('email');
        vm.pagination.page = 2;
        vm.query = 'foo';
        vm.pagination.itemsPerPage = 4;
        vm.sort = 'ASC';

        User.query.and.returnValue($q.when({total: 50, users: [{}, {}]}));

        vm.pageChanged();

        $rootScope.$apply();

        expect(vm.pagination.page).toEqual(2);
        expect(vm.pagination.total).toEqual(2);

        User.query.and.returnValue($q.when({total: 50, users: [{}, {}]}));

        vm.perPage(2);

        $rootScope.$apply();

        expect(vm.pagination.page).toEqual(3);
        expect(vm.pagination.total).toEqual(2);
    });

    it('view users goes to /admin/user/:id', function () {

        vm.viewUser(555);
        expect($location.path()).toEqual('/admin/user/555');
    });

});

