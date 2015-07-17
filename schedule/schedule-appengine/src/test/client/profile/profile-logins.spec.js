describe('ProfileLoginsController', function () {
    var $scope, vm;

    beforeEach(module('jasifyWebTest'));


    beforeEach(inject(function ($rootScope, $controller) {
        $scope = $rootScope.$new();
        vm = $controller('ProfileLoginsController', {
            $scope: $scope,
            logins: ['a', 'b', 'c']
        });
    }));

    it('knows icons for providers', function () {
        expect(vm.icon({provider: 'Google'})).toEqual('ion-social-google');
        expect(vm.icon({provider: 'Facebook'})).toEqual('ion-social-facebook');
    });

});
