describe('ProfileLoginsController', function () {
    var $scope, vm;

    beforeEach(module('jasify'));


    beforeEach(inject(function ($rootScope, $controller) {
        $scope = $rootScope.$new();
        vm = $controller('ProfileLoginsController', {
            $scope: $scope,
            logins: ['a', 'b', 'c']
        });
    }));

    it('captures logins from injection', function () {
        expect(vm.logins).toBeDefined();
        expect(vm.logins[0]).toEqual('a');
        expect(vm.logins[1]).toEqual('b');
        expect(vm.logins[2]).toEqual('c');
    });

    it('knows icons for providers', function () {
        expect(vm.icon({provider: 'Google'})).toEqual('ion-social-google');
        expect(vm.icon({provider: 'Facebook'})).toEqual('ion-social-facebook');
    });

});
