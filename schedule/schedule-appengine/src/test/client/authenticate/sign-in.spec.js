describe('SignInController', function () {
    var vm;
    beforeEach(module('jasifyWeb'));

    beforeEach(inject(function ($controller) {
        vm = $controller('SignInController');
    }));

    it('is not on e-mail mode when instantiated', function () {
        expect(vm.email).toBe(false);
    });

    it('initializes user', function () {
        expect(typeof vm.user).toEqual('object');
    });

    it('initializes rememberMe', function () {
        expect(vm.rememberMe).toBe(false);
    });


});
