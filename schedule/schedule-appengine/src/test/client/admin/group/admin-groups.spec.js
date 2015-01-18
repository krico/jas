describe('AdminGroupsController', function () {
    var $controller, $location, Group, vm, $q, $rootScope;

    beforeEach(module('jasify'));


    beforeEach(inject(function (_$controller_, _$location_, _Group_, _$q_, _$rootScope_) {
        $controller = _$controller_;
        $location = _$location_;
        Group = _Group_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    var expectedGroups = [];

    beforeEach(function () {
        vm = $controller('AdminGroupsController', {$location: $location, Group: Group, groups: expectedGroups});
    });


    it('needs to be tested', function () {
        //TODO: wszarmach could write these tests
    });

});

