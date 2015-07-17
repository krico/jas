describe('AdminOrganizationController', function () {
    var $controller, $location, Organization, vm, $q, $rootScope;

    beforeEach(module('jasifyWebTest'));


    beforeEach(inject(function (_$controller_, _$location_, _Organization_, _$q_, _$rootScope_) {
        $controller = _$controller_;
        $location = _$location_;
        Organization = _Organization_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    var expectedOrganization = {};

    beforeEach(function () {
        vm = $controller('AdminOrganizationController', {
            Organization: Organization,
            organization: expectedOrganization
        });
        $rootScope.$apply();
    });


    it('needs to be tested', function () {
        //TODO: wszarmach could write these tests
    });

});

