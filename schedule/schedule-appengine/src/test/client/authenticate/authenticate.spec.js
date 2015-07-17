describe('AuthenticateController', function () {
    var vm;
    beforeEach(module('jasifyWebTest'));

    beforeEach(inject(function ($controller) {
        vm = $controller('AuthenticateController', {$scope: {}} );
    }));

    it('switches between "Sign In" and "Create Account"', function () {
        for (var i = 0; i < 2; ++i) {
            vm.switchToSignIn();
            expect(vm.isSignIn()).toBe(true);
            expect(vm.isCreateAccount()).toBe(false);
            vm.switchToCreateAccount();
            expect(vm.isSignIn()).toBe(false);
            expect(vm.isCreateAccount()).toBe(true);
        }
    });

});
