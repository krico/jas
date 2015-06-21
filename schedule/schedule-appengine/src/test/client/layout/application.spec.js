describe('ApplicationController', function () {
    var $controller, $rootScope, $location, $modalMock, $scope, vm, AUTH_EVENTS, $window = {};

    beforeEach(module('jasifyWeb'));
    beforeEach(module('jasify.mocks'));

    beforeEach(inject(function (_$controller_, _$rootScope_, _$location_, _$modalMock_, _AUTH_EVENTS_) {
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        $modalMock = _$modalMock_;
        AUTH_EVENTS = _AUTH_EVENTS_;

        $scope = $rootScope.$new();
        vm = $controller('ApplicationController', {$scope: $scope, $window : $window});
    }));

    it('can determine if a menu is active ', function () {

        expect(vm.menuActive('/profile')).toBe(false);

        $location.path('/profile');

        expect(vm.menuActive('/profile')).toBe('active');

    });
});
