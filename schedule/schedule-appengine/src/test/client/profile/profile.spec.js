describe('ProfileController', function () {
    var $scope, $applicationScope, vm, $routeParams, User, Session, $controller, $rootScope, $q;

    beforeEach(module('jasifyWeb'));

    beforeEach(inject(function (_$routeParams_, _User_, _Session_, _$controller_, _$rootScope_, _$q_) {
        $routeParams = _$routeParams_;
        $controller = _$controller_;
        User = _User_;
        Session = _Session_;
        $rootScope = _$rootScope_;
        $q = _$q_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {$scope: $applicationScope});
        spyOn(User, 'get');

    }));

    var construct = function () {
        Session.create(1, 555);
        User.get.and.returnValue($q.when({id: 555, name: 'test'}));

        vm = $controller('ProfileController', {
            $scope: $scope
        });
    };

    beforeEach(function () {
        $scope = $applicationScope.$new();
        construct();
    });

    it('knows if isWelcome is tru or note', function () {
        $rootScope.$apply(); //load the user

        $routeParams.extra = 'welcome';
        construct();
        $rootScope.$apply(); //load the user
        expect(vm.isWelcome()).toEqual(true);

        $routeParams.extra = 'foo';
        construct();
        $rootScope.$apply(); //load the user
        expect(vm.isWelcome()).toEqual(false);
    });

    it('sets extra to false when there are not route parameters', function () {
        $rootScope.$apply(); //load the user
        expect(vm.isWelcome()).toEqual(false);
    });

    it('can handle alerts', function () {
        $rootScope.$apply(); //load the user

        expect(vm.alerts.length).toEqual(0);
        vm.alert('success', 'alert text');

        expect(vm.alerts.length).toEqual(1);
        expect(vm.alerts[0].type).toEqual('success');
        expect(vm.alerts[0].msg).toEqual('alert text');

    });

    it('loads user when constructed', function () {
        expect(vm.user.id).not.toBeDefined();
        $rootScope.$apply(); //load the user
        expect(vm.user.id).toEqual(555);
        expect(vm.user).toBeDefined();
        expect(vm.user.name).toEqual('test');
    });

    it('saves the user and updates currentUser and calls setPristine', function () {
        $rootScope.$apply(); //load the user

        vm.user.about = 'about him';


        spyOn(User, 'update').and.returnValue($q.when(vm.user));
        var called = null;

        vm.profileForm = {
            $setPristine: function () {
                called = true;
            }
        };


        vm.save();

        $rootScope.$apply();

        expect($scope.currentUser.about).toEqual('about him');
        expect(called).toBe(true);
    });

    it('resets to original user ', function () {
        $rootScope.$apply(); //load the user

        vm.user.about = 'about him';

        User.get.and.returnValue($q.when({id: 555, name: 'test'}));

        vm.reset();

        $rootScope.$apply();

        expect(vm.user.id).toEqual(555);
        expect(vm.user.about).not.toBeDefined();
    });

    it('calls profileForm.$setPristine on reset ', function () {
        $rootScope.$apply(); //load the user

        User.get.and.returnValue($q.when({id: 555, name: 'test'}));

        var called = null;
        vm.profileForm = {
            $setPristine: function () {
                called = true;
            }
        };

        vm.reset();

        $rootScope.$apply();

        expect(called).toEqual(true);

    });

});
