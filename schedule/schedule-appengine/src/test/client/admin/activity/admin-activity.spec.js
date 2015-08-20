describe('AdminActivityController', function () {
    var $scope, $controller, $location, $q, $rootScope;

    beforeEach(module('jasifyWebTest'));

    beforeEach(inject(function (_$controller_, _$location_, _$q_, _$rootScope_, _$moment_) {
        $controller = _$controller_;
        $location = _$location_;
        $q = _$q_;
        $rootScope = _$rootScope_;
        $moment = _$moment_;
    }));

    beforeEach(function () {
        $scope = $rootScope.$new();
        $rootScope.$apply();
    });

    describe('Activity initialisations', function () {
        it('Activity.id is undefined on create', function () {
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {},
                organizations: []
            });

            expect(vm.activity).toBeDefined();
            expect(vm.activity.id).toBeUndefined();
        });

        it('Activity.id is defined on update', function () {
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {id: 123},
                organizations: []
            });

            expect(vm.activity).toBeDefined();
            expect(vm.activity.id).toBeDefined();
        });
    });

    describe('Organization initialisations', function () {
        it('Organization is undefined when organizations is empty', function () {
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {},
                organizations: []
            });

            expect(vm.organization).toBeUndefined();
        });

        it('Organization is undefined when organizations contains multiple elements', function () {
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {},
                organizations: {items: [{id: 100}, {id: 200}]}
            });

            expect(vm.organization).toBeUndefined();
        });

        it('Organization is defined when organizations is contains one element', function () {
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {},
                organizations: {items: [{id: 100}]}
            });

            expect(vm.organization).toBeDefined();
        });
    });

    describe('Activity time watch updates', function () {
        it('Start hour before finish hour sets finish to same value as start', function () {
            // We dont allow dates in the past... I would like to fix this test when it breaks
            var start = $moment().set('day', 1).set('month', 1).set('year', 2999).set('hour', 12).format();
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {id: 123, activityType: {organizationId: 100}, start: start},
                organizations: {items: [{id: 100}]}
            });

            vm.activity.start = $moment(vm.activity.start).subtract('day', 1).format();

            $scope.$digest();

            expect(vm.activity.finish).toBeDefined();
            expect(vm.activity.finish).toEqual(vm.activity.start);
        });

        it('Start hour before finish hour sets finish to same value as start with 1 days difference', function () {
            var start = $moment().set('day', 1).set('month', 1).set('year', 2999).set('hour', 12).format();
            var finish = $moment().set('day', 2).set('month', 1).set('year', 2999).set('hour', 11).format();
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {id: 123, activityType: {organizationId: 100}, start: start, finish: finish},
                organizations: {items: [{id: 100}]}
            });

            vm.activity.start = $moment(vm.activity.start).subtract('day', 1).format();

            $scope.$digest();

            expect(vm.activity.finish).toEqual(start);
        });

        it('Finish time update changes minDates', function () {
            var finish = $moment().set('day', 2).set('month', 1).set('year', 2999).set('hour', 11).format();
            var vm = $controller('AdminActivityController', {
                $scope: $scope,
                activity: {id: 123, activityType: {organizationId: 100}, finish: finish},
                organizations: {items: [{id: 100}]}
            });

            vm.activity.finish = $moment(vm.activity.finish).subtract('day', 1).format();

            $scope.$digest();

            expect(vm.repeatUntilDateOptions.minDate).toEqual(vm.activity.finish);
            expect(vm.toDateOptions.minDate).toEqual(vm.activity.finish);
        });
    });
});