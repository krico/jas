describe('SignInController', function () {
    var vm;
    beforeEach(module('jasify'));

    beforeEach(inject(function ($controller) {
        vm = $controller('SignInController');
    }));

    it('is not on e-mail mode when instantiated', function () {
        expect(vm.isEmail()).toBe(false);
    });

    it('switches to email', function () {
        vm.withEmail();
        expect(vm.isEmail()).toBe(true);
    });

    it('initializes user', function () {
        expect(typeof vm.user).toEqual('object');
    });

    it('initializes rememberMe', function () {
        expect(vm.rememberMe).toBe(false);
    });


});
