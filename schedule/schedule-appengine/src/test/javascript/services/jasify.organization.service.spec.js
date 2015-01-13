describe('OrganizationService', function () {
    var Organization, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_Organization_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        Organization = _Organization_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should query all organizations', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'query').and.returnValue($q.when({result: {items: expected}}));
        Organization.query()
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.query).toHaveBeenCalled();
    });

    it('should get organization by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.organizations, 'get').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Organization.get(id)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.get).toHaveBeenCalledWith({id: id});
    });

    it('should update organization', function () {
        var expected = {name: "Organization"};
        spyOn($gapiMock.client.jasify.organizations, 'update').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Organization.update(expected)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.update).toHaveBeenCalledWith(expected);
    });

    it('should add organization', function () {
        var expected = {name: "Organization"};
        spyOn($gapiMock.client.jasify.organizations, 'add').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Organization.add(expected)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.add).toHaveBeenCalledWith(expected);
    });

    it('should list users', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'users').and.returnValue($q.when({result: {items: expected}}));

        var id = "abc";
        Organization.users(id)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.users).toHaveBeenCalledWith({id: id});
    });

    it('should list groups', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'groups').and.returnValue($q.when({result: {items: expected}}));

        var id = "abc";
        Organization.groups(id)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.groups).toHaveBeenCalledWith({id: id});
    });

    it('should add user', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'addUser').and.returnValue($q.when({result: false}));

        var o = {};
        var u = {};
        var id = "abc";
        Organization.addUser(o, u)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.addUser).toHaveBeenCalledWith({organization: o, user: u});
    });

    it('should remove organization', function () {
        var expected = {name: "Organization"};
        spyOn($gapiMock.client.jasify.organizations, 'remove').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Organization.remove(id)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.remove).toHaveBeenCalledWith({id: id});
    });

});
