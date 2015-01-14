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

    it('should add user by id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'addUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Organization.addUser(o, u)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.addUser).toHaveBeenCalledWith({
            organizationId: o.id,
            userId: u.id
        });
    });

    it('should add user by id regardless of object or id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.organizations, 'addUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Organization.addUser(o.id, u.id)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.addUser).toHaveBeenCalledWith({
            organizationId: o.id,
            userId: u.id
        });
    });

    it('should remove user by id', function () {
        spyOn($gapiMock.client.jasify.organizations, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Organization.removeUser(o, u)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.removeUser).toHaveBeenCalledWith({
            organizationId: o.id,
            userId: u.id
        });
    });

    it('should remove user by id regardless of object or id', function () {
        spyOn($gapiMock.client.jasify.organizations, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Organization.removeUser(o.id, u.id)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.organizations.removeUser).toHaveBeenCalledWith({
            organizationId: o.id,
            userId: u.id
        });
    });

    it('should support remove user with array of users', function () {
        spyOn($gapiMock.client.jasify.organizations, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = [{id: 'sas'}, {id: 'sas1'}];

        Organization.removeUser(o.id, u)
            .then(function (res) {
                expect(res).toEqual([false, false]);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        for (var i = 0; i < u.length; ++i) {

            expect($gapiMock.client.jasify.organizations.removeUser).toHaveBeenCalledWith({
                organizationId: o.id,
                userId: u[i].id
            });
        }
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
