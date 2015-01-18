describe('AdminOrganizationsController', function () {
    var $controller, $location, Organization, vm, $q, $rootScope;

    beforeEach(module('jasify'));


    beforeEach(inject(function (_$controller_, _$location_, _Organization_, _$q_, _$rootScope_) {
        $controller = _$controller_;
        $location = _$location_;
        Organization = _Organization_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    var expectedOrganizations = [];

    beforeEach(function () {
        vm = $controller('AdminOrganizationsController', {$location: $location, Organization: Organization, organizations: expectedOrganizations});
    });


    it('needs to be tested', function () {
        //TODO: wszarmach could write these tests
    });

});

