describe('ProfileLoginsController', function () {
    var $scope, controller;

    beforeEach(module('jasify'));


    beforeEach(inject(function ($rootScope, $controller) {
        $scope = $rootScope.$new();
        controller = $controller('ProfileLoginsController', {
            $scope: $scope,
            logins: ['a', 'b', 'c']
        });
    }));

    it('captures logins from injection', function () {
        expect($scope.logins).toBeDefined();
        expect($scope.logins[0]).toEqual('a');
        expect($scope.logins[1]).toEqual('b');
        expect($scope.logins[2]).toEqual('c');
    });

    it('knows icons for providers', function () {
        expect($scope.icon({provider: 'Google'})).toEqual('ion-social-google');
        expect($scope.icon({provider: 'Facebook'})).toEqual('ion-social-facebook');
    });

});
