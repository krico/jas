describe('ActivityPackageService', function () {
    var ActivityPackage, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_ActivityPackage_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        ActivityPackage = _ActivityPackage_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should query all activityPackages by organization id', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.activityPackages, 'query').and.returnValue($q.when({result: {items: expected}}));
        var id = 'abc';
        ActivityPackage.query(id)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.query).toHaveBeenCalledWith({organizationId: id});
    });

    it('should query all activityPackages by organization', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.activityPackages, 'query').and.returnValue($q.when({result: {items: expected}}));
        var org = {id: 'abc', name: 'foo'};
        ActivityPackage.query(org)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.query).toHaveBeenCalledWith({organizationId: org.id});
    });

    it('should get activityPackage by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.activityPackages, 'get').and.returnValue($q.when({result: expected}));

        var id = "abc";
        ActivityPackage.get(id)
            .then(function (activityPackage) {
                expect(activityPackage).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.get).toHaveBeenCalledWith({id: id});
    });

    it('should update activityPackage', function () {
        var expected = {name: "ActivityPackage"};
        spyOn($gapiMock.client.jasify.activityPackages, 'update').and.returnValue($q.when({result: expected}));

        ActivityPackage.update(expected)
            .then(function (activityPackage) {
                expect(activityPackage).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.update).toHaveBeenCalledWith(expected);
    });

    it('should add activityPackage', function () {
        var expectedActivityPackage = {name: "ActivityPackage"};
        var activityPackage = {id: 'ap'};
        var activities = [{id: 'one'}];
        spyOn($gapiMock.client.jasify.activityPackages, 'add').and.returnValue($q.when({result: expectedActivityPackage}));

        ActivityPackage.add(activityPackage, activities)
            .then(function (at) {
                expect(at).toBe(expectedActivityPackage);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.add).toHaveBeenCalledWith({
            activityPackage: activityPackage,
            activities: activities
        });
    });

    it('should remove activityPackage by id', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'remove').and.returnValue($q.when({result: false}));

        var id = 'xyz';
        ActivityPackage.remove(id)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.remove).toHaveBeenCalledWith({id: id});
    });

    it('should remove activityPackage ', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'remove').and.returnValue($q.when({result: false}));

        var at = {id: 'xyz', name: 'foo'};
        ActivityPackage.remove(at)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.remove).toHaveBeenCalledWith({id: at.id});
    });

    it('should add activity to activityPackage by id', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'addActivity').and.returnValue($q.when({result: false}));
        ActivityPackage.addActivity('anActivityPackageId', 'anActivityId')
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.addActivity)
            .toHaveBeenCalledWith({activityPackageId: 'anActivityPackageId', activityId: 'anActivityId'});
    });

    it('should add activity to activityPackage with objects', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'addActivity').and.returnValue($q.when({result: false}));
        ActivityPackage.addActivity({id: 'anActivityPackageId'}, {id: 'anActivityId'})
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.addActivity)
            .toHaveBeenCalledWith({activityPackageId: 'anActivityPackageId', activityId: 'anActivityId'});
    });

    it('should remove activity to activityPackage by id', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'removeActivity').and.returnValue($q.when({result: false}));
        ActivityPackage.removeActivity('anActivityPackageId', 'anActivityId')
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.removeActivity)
            .toHaveBeenCalledWith({activityPackageId: 'anActivityPackageId', activityId: 'anActivityId'});
    });

    it('should remove activity to activityPackage with objects', function () {
        spyOn($gapiMock.client.jasify.activityPackages, 'removeActivity').and.returnValue($q.when({result: false}));
        ActivityPackage.removeActivity({id: 'anActivityPackageId'}, {id: 'anActivityId'})
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activityPackages.removeActivity)
            .toHaveBeenCalledWith({activityPackageId: 'anActivityPackageId', activityId: 'anActivityId'});
    });

});
