describe('NavbarController', function () {
    var $controller, $rootScope, $location, $scope, $applicationScope, vm, AUTH_EVENTS;

    beforeEach(module('jasifyWebTest'));

    beforeEach(inject(function (_$controller_, _$rootScope_, _$location_, _AUTH_EVENTS_) {
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $location = _$location_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        $applicationScope = $rootScope.$new();
        $controller('ApplicationController', {
            $scope: $applicationScope
        });
        $scope = $applicationScope.$new();
        vm = $controller('NavbarController', {
            $scope: $scope
        });
    }));

    it('starts with a collapsed navbar ', function () {
        expect(vm.navbarCollapsed).toBe(true);
    });

    it('can toggle the collapse status of the navbar ', function () {

        expect(vm.navbarCollapsed).toBe(true);
        vm.toggleCollapse();
        expect(vm.navbarCollapsed).toBe(false);
        vm.toggleCollapse();
        expect(vm.navbarCollapsed).toBe(true);

    });

    it('can collapse the navbar ', function () {

        expect(vm.navbarCollapsed).toBe(true);
        vm.toggleCollapse();
        expect(vm.navbarCollapsed).toBe(false);
        vm.collapse();
        expect(vm.navbarCollapsed).toBe(true);

    });

    it('should register a loginSucceeded as a listener for the login event', function () {

        $scope = $rootScope.$new();
        spyOn($scope, '$on');
        vm = $controller('NavbarController', {
            $scope: $scope});
        expect($scope.$on).toHaveBeenCalledWith(AUTH_EVENTS.loginSuccess, vm.loginSucceeded);

    });

    it('should register a logoutSucceeded as a listener for the logout event', function () {

        $scope = $rootScope.$new();
        spyOn($scope, '$on');
        vm = $controller('NavbarController', {
            $scope: $scope
        });
        expect($scope.$on).toHaveBeenCalledWith(AUTH_EVENTS.logoutSuccess, vm.logoutSucceeded);

    });

    it('should watch the $location.path', function () {
        $location.path('/tmp');
        $rootScope.$digest();
        expect(vm.path).toEqual('/tmp');
        $location.path('/tmp2');
        expect(vm.path).toEqual('/tmp');
        $rootScope.$digest();
        expect(vm.path).toEqual('/tmp2');
    });
});
