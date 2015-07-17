describe('AdminGroupController', function () {
    var $controller, $location, Group, vm, $q, $rootScope;

    beforeEach(module('jasifyWebTest'));


    beforeEach(inject(function (_$controller_, _$location_, _Group_, _$q_, _$rootScope_) {
        $controller = _$controller_;
        $location = _$location_;
        Group = _Group_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    var expectedGroup = {};

    beforeEach(function () {
        vm = $controller('AdminGroupController', {Group: Group, group: expectedGroup});
        $rootScope.$apply();
    });


    it('needs to be tested', function () {
        //TODO: wszarmach could write these tests
    });

});

