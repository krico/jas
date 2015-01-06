describe('UserService', function () {
    var User, Endpoint, $rootScope, $gapiMock, $q;

    beforeEach(module('jasify'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_User_, _Endpoint_, _$rootScope_, _$gapiMock_, _$q_) {
        User = _User_;
        Endpoint = _Endpoint_;
        $rootScope = _$rootScope_;
        $gapiMock = _$gapiMock_;
        $q = _$q_;
        Endpoint.jasifyLoaded();
    }));

    it('should get a user by id', function () {

        var params = {id: 555};
        spyOn($gapiMock.client.jasify.users, 'get').and.returnValue($q.when({result: {name: 'u'}}));
        User.get(params.id)
            .then(function (user) {
                expect(user.name).toEqual('u');
            },
            function () {
                fail();
            });

        $rootScope.$apply();

        expect($gapiMock.client.jasify.users.get).toHaveBeenCalledWith(params);
    });

    it('should handle failed get by id', function () {

        spyOn($gapiMock.client.jasify.users, 'get').and.returnValue($q.reject());

        var ok = null;
        User.get(111)
            .then(function () {
                fail();
            }, function () {
                ok = true;
            });

        $rootScope.$apply();

        expect(ok).toBe(true);
    });

    it('should update a user', function () {
        var user = {id: "abc", name: 'name'};
        spyOn($gapiMock.client.jasify.users, 'update').and.returnValue($q.when({result: user}));

        var ok = null;
        User.update(user).then(function () {
            ok = true;
        }, function () {
            fail();
        });

        $rootScope.$apply();

        expect(ok).toBe(true);
        expect($gapiMock.client.jasify.users.update).toHaveBeenCalledWith(user);
    });

    it('should query users', function () {

        var result = [{x: 'y'}];
        spyOn($gapiMock.client.jasify.users, 'query').and.returnValue($q.when({result: result}));

        var users = null;
        var params = {limit: 1, offset: 2};
        User.query(params).then(function (r) {
            users = r;
        }, function () {
            fail();
        });

        $rootScope.$apply();

        expect(users).toEqual(result);
        expect($gapiMock.client.jasify.users.query).toHaveBeenCalledWith(params);

    });

    it('should add users', function () {

        var result = {new: 'user'};
        spyOn($gapiMock.client.jasify.users, 'add').and.returnValue($q.when({result: result}));

        var user = {};
        var created = null;
        var password = 'xyz';
        User.add(user, password).then(function (r) {
            created = r;
        }, function () {
            fail();
        });

        $rootScope.$apply();

        expect(created).toEqual(result);
        expect($gapiMock.client.jasify.users.add).toHaveBeenCalledWith({user: user, password: password});

    });

});
