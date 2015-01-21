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

    it('initializes authenticateForm', function () {
        expect(typeof vm.authenticateForm).toEqual('object');
    });

    it('has a function to handle password strength callback', function () {
        expect(typeof vm.passwordStrengthCallback).toEqual('function');
    });

    it('sets popoverText text when passwordStrength changes', function () {
        var unset = 'Choose a password.';
        var weak = 'Weak!';
        var avg = 'Average...';
        var ok = 'Good!';
        var full = 'Excellent!!!';
        var i;
        expect(vm.popoverText).toEqual('');
        for (i = -10; i <= 0; ++i) {
            vm.passwordStrengthCallback(i);
            expect(vm.popoverText).toEqual(unset);
        }
        for (i = 1; i <= 15; ++i) {
            vm.passwordStrengthCallback(i);
            expect(vm.popoverText).toEqual(weak);
        }
        for (i = 16; i <= 40; ++i) {
            vm.passwordStrengthCallback(i);
            expect(vm.popoverText).toEqual(avg);
        }
        for (i = 41; i <= 80; ++i) {
            vm.passwordStrengthCallback(i);
            expect(vm.popoverText).toEqual(ok);
        }
        for (i = 81; i <= 120; ++i) {
            vm.passwordStrengthCallback(i);
            expect(vm.popoverText).toEqual(full);
        }
    });


});
