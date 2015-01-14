describe('GroupService', function () {
    var Group, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_Group_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        Group = _Group_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should query all groups', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.groups, 'query').and.returnValue($q.when({result: {items: expected}}));
        Group.query()
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.query).toHaveBeenCalled();
    });

    it('should get group by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.groups, 'get').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Group.get(id)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.get).toHaveBeenCalledWith({id: id});
    });

    it('should update group', function () {
        var expected = {name: "Group"};
        spyOn($gapiMock.client.jasify.groups, 'update').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Group.update(expected)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.update).toHaveBeenCalledWith(expected);
    });

    it('should add group', function () {
        var expected = {name: "Group"};
        spyOn($gapiMock.client.jasify.groups, 'add').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Group.add(expected)
            .then(function (org) {
                expect(org).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.add).toHaveBeenCalledWith(expected);
    });

    it('should list users', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.groups, 'users').and.returnValue($q.when({result: {items: expected}}));

        var id = "abc";
        Group.users(id)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.users).toHaveBeenCalledWith({id: id});
    });

    it('should add user by id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.groups, 'addUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Group.addUser(o, u)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.addUser).toHaveBeenCalledWith({
            groupId: o.id,
            userId: u.id
        });
    });

    it('should add user by id regardless of object or id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.groups, 'addUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Group.addUser(o.id, u.id)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.addUser).toHaveBeenCalledWith({
            groupId: o.id,
            userId: u.id
        });
    });

    it('should remove user by id', function () {
        spyOn($gapiMock.client.jasify.groups, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Group.removeUser(o, u)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.removeUser).toHaveBeenCalledWith({
            groupId: o.id,
            userId: u.id
        });
    });

    it('should remove user by id regardless of object or id', function () {
        spyOn($gapiMock.client.jasify.groups, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = {id: 'sas'};

        Group.removeUser(o.id, u.id)
            .then(function (res) {
                expect(res).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.groups.removeUser).toHaveBeenCalledWith({
            groupId: o.id,
            userId: u.id
        });
    });

    it('should support remove user with array of users', function () {
        spyOn($gapiMock.client.jasify.groups, 'removeUser').and.returnValue($q.when({result: false}));

        var o = {id: 45};
        var u = [{id: 'sas'}, {id: 'sas1'}];

        Group.removeUser(o.id, u)
            .then(function (res) {
                expect(res).toEqual([false, false]);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        for (var i = 0; i < u.length; ++i) {

            expect($gapiMock.client.jasify.groups.removeUser).toHaveBeenCalledWith({
                groupId: o.id,
                userId: u[i].id
            });
        }
    });

});
