describe('CreateAccountController', function () {
    var vm;
    beforeEach(module('jasify'));

    beforeEach(inject(function ($controller) {
        vm = $controller('CreateAccountController');
    }));

    it('is not on e-mail mode when instantiated', function () {
        expect(vm.isEmail()).toBe(false);
    });

    it('switches to email', function () {
        vm.withEmail();
        expect(vm.isEmail()).toBe(true);
    });

    it('switches to OAuth', function () {
        vm.withEmail();
        vm.withOAuth();
        expect(vm.isEmail()).toBe(false);
    });

    it('initializes user', function () {
        expect(typeof vm.user).toEqual('object');
    });


});
