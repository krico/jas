describe('ActivityService', function () {
    var Activity, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasifyComponents'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_Activity_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        Activity = _Activity_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should query all activities with parameters', function () {
        var expected = [];
        spyOn($gapiMock.client.jasify.activities, 'query').and.returnValue($q.when({result: {items: expected}}));
        var param = {
            /* Either one or the other */
            organizationId: 'xxx',
            activityTypeId: 'zzz',
            fromDate: new Date(),
            toDate: new Date(),
            offset: 25,
            limit: 100
        };
        Activity.query(param)
            .then(function (res) {
                expect(res.items).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.query).toHaveBeenCalledWith(param);
    });

    it('should get activity by id', function () {
        var expected = {};
        spyOn($gapiMock.client.jasify.activities, 'get').and.returnValue($q.when({result: expected}));

        var id = "abc";
        Activity.get(id)
            .then(function (activity) {
                expect(activity).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.get).toHaveBeenCalledWith({id: id});
    });

    it('should update activity', function () {
        var expected = {name: "Activity"};
        spyOn($gapiMock.client.jasify.activities, 'update').and.returnValue($q.when({result: expected}));

        Activity.update(expected)
            .then(function (activity) {
                expect(activity).toBe(expected);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.update).toHaveBeenCalledWith(expected);
    });

    it('should add activity', function () {
        var expectedActivity = {name: "Activity"};
        spyOn($gapiMock.client.jasify.activities, 'add').and.returnValue($q.when({result: expectedActivity}));

        Activity.add(expectedActivity)
            .then(function (at) {
                expect(at).toBe(expectedActivity);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.add).toHaveBeenCalledWith({activity:expectedActivity});
    });

    it('should add activity with repeatDetails', function () {
        var expectedActivity = {name: "Activity"};
        var expectedRepeatDetails = {name: "RepeatDetails"};
        spyOn($gapiMock.client.jasify.activities, 'add').and.returnValue($q.when({result: expectedActivity}));

        Activity.add(expectedActivity, expectedRepeatDetails)
            .then(function (at) {
                expect(at).toBe(expectedActivity);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.add).toHaveBeenCalledWith({activity:expectedActivity, repeatDetails:expectedRepeatDetails});
    });

    it('should remove activity by id', function () {
        spyOn($gapiMock.client.jasify.activities, 'remove').and.returnValue($q.when({result: false}));

        var id = 'xyz';
        Activity.remove(id)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.remove).toHaveBeenCalledWith({id: id});
    });

    it('should remove activity ', function () {
        spyOn($gapiMock.client.jasify.activities, 'remove').and.returnValue($q.when({result: false}));

        var at = {id: 'xyz', name: 'foo'};
        Activity.remove(at)
            .then(function (at) {
                expect(at).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activities.remove).toHaveBeenCalledWith({id: at.id});
    });

    it('should subscribe to activity ', function () {
        spyOn($gapiMock.client.jasify.activitySubscriptions, 'add').and.returnValue($q.when({result: false}));

        var user = {id: 'xyz', name: 'foo'};
        var activity = {id: 'klm', name: 'bar'};
        Activity.subscribe(user, activity)
            .then(function (whatever) {
                expect(whatever).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activitySubscriptions.add).toHaveBeenCalledWith({
            userId: user.id,
            activityId: activity.id
        });
    });

    it('should know if user is subscribed to activity ', function () {
        spyOn($gapiMock.client.jasify.activitySubscriptions, 'query').and.returnValue($q.when({result: false}));

        var user = {id: 'xyz', name: 'foo'};
        var activity = {id: 'klm', name: 'bar'};
        Activity.isSubscribed(user, activity)
            .then(function (whatever) {
                expect(whatever).toBe(false);
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.activitySubscriptions.query).toHaveBeenCalledWith({
            userId: user.id,
            activityId: activity.id
        });
    });

});
