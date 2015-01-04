describe('HomeController', function () {
    var vm;
    beforeEach(module('jasify'));

    beforeEach(inject(function ($controller) {
        vm = $controller('HomeController');
    }));

    it('is home', function () {
        expect(vm.home).toBe(true);
    });

});
