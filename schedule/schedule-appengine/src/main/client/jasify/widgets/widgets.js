/*global window */
(function (angular) {

    "use strict";

    angular.module('jasifyWeb').controller('WidgetsController', WidgetsController);


    function WidgetsController(angularLoad, organizations) {
        var vm = this;

        vm.organizations = organizations.items;

        if (vm.organizations.length === 1) {
            vm.organization = vm.organizations[0];
            organizationSelected(vm.organization);
        }

        vm.organizationSelected = organizationSelected;

        function organizationSelected(organization) {
            removeElement(document.getElementById('booking-with-jasify-script'));
            angularLoad.loadScript(
                'booking-with-jasify-script',
                'booking-via-jasify.js?organizationId=' + organization.id + '&_=' + new Date().getTime()
            );
        }

        function removeElement(element) {
            if (element && element.parentNode) {
                element.parentNode.removeChild(element);
            }
        }
    }

})(window.angular);