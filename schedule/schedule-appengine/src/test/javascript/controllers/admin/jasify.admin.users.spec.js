describe('AdminUsersController', function () {
    var $controller, $location, User, vm, $q, $rootScope;

    beforeEach(module('jasify'));


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

    it('pageChanged passes query parameters and reads X-Total', function () {
        vm.searchBy = 'email';
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 5;
        vm.sort = 'ASC';

        User.query.and.returnValue($q.when({total: 50, users: []}));

        vm.pageChanged();

        $rootScope.$apply();

        expect(vm.page).toEqual(2);
        expect(vm.total).toEqual(50);
        expect(User.query).toHaveBeenCalledWith({
            field: vm.searchBy,
            offset: (vm.page * vm._perPage),
            limit: vm._perPage,
            sort: vm.sort,
            query: vm.query
        });
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

        User.query.and.returnValue($q.when({total: 50, users: []}));

        vm.queryChanged();

        $rootScope.$apply();

        expect(vm.page).toEqual(1);
        expect(vm.total).toEqual(50);
    });

    it('perPage queries and stays on same record', function () {
        vm.searchBy = 'email';
        vm.page = 2;
        vm.query = 'foo';
        vm._perPage = 4;
        vm.sort = 'ASC';

        User.query.and.returnValue($q.when({total: 50, users: []}));

        vm.pageChanged();

        $rootScope.$apply();

        expect(vm.page).toEqual(2);
        expect(vm.total).toEqual(50);

        User.query.and.returnValue($q.when({total: 50, users: []}));

        vm.perPage(2);

        $rootScope.$apply();

        expect(vm.page).toEqual(3);
        expect(vm.total).toEqual(50);
    });

    it('view users goes to /admin/user/:id', function () {

        vm.viewUser(555);
        expect($location.path()).toEqual('/admin/user/555');
    });

});

