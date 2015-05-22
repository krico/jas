describe('ActivityTypeService', function () {
    var ActivityType, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_ActivityType_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        ActivityType = _ActivityType_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('ActivityType.query always returns array', function () {
        spyOn($gapiMock.client.jasify.activityTypes, 'query').and.returnValue($q.when({result: {}}));
        var id = 'abc';
        ActivityType.query(id)
            .then(function (res) {
                expect(res.items).toBeDefined();
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.query).toHaveBeenCalledWith({organizationId: id});
    });

    it('should query all activityTypes by organization id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.activityTypes, 'query').and.returnValue($q.when({result: {items: expected}}));
        var id = 'abc';
        ActivityType.query(id)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.query).toHaveBeenCalledWith({organizationId: id});
    });

    it('should query all activityTypes by organization', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.activityTypes, 'query').and.returnValue($q.when({result: {items: expected}}));
        var org = {id: 'abc', name: 'foo'};
        ActivityType.query(org)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.query).toHaveBeenCalledWith({organizationId: org.id});
    });

    it('should get activityType by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.activityTypes, 'get').and.returnValue($q.when({result: expected}));

        var id = "abc";
        ActivityType.get(id)
            .then(function (activityType) {
                expect(activityType).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.get).toHaveBeenCalledWith({id: id});
    });

    it('should update activityType', function () {
        var expected = {name: "ActivityType"};
        spyOn($gapiMock.client.jasify.activityTypes, 'update').and.returnValue($q.when({result: expected}));

        ActivityType.update(expected)
            .then(function (activityType) {
                expect(activityType).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.update).toHaveBeenCalledWith(expected);
    });

    it('should add activityType under organization', function () {
        var expectedActivityType = {name: "ActivityType"};
        var expectedOrgId = 'oid';
        spyOn($gapiMock.client.jasify.activityTypes, 'add').and.returnValue($q.when({result: expectedActivityType}));

        ActivityType.add(expectedOrgId, expectedActivityType)
            .then(function (at) {
                expect(at).toBe(expectedActivityType);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.add).toHaveBeenCalledWith({
            organizationId: expectedOrgId,
            activityType: expectedActivityType
        });
    });

    it('should remove activityType by id', function () {
        spyOn($gapiMock.client.jasify.activityTypes, 'remove').and.returnValue($q.when({result: false}));

        var id = 'xyz';
        ActivityType.remove(id)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.remove).toHaveBeenCalledWith({id: id});
    });
    it('should remove activityType ', function () {
        spyOn($gapiMock.client.jasify.activityTypes, 'remove').and.returnValue($q.when({result: false}));

        var at = {id: 'xyz', name: 'foo'};
        ActivityType.remove(at)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityTypes.remove).toHaveBeenCalledWith({id: at.id});
    });

});
