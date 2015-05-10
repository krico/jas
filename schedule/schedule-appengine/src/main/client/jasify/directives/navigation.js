/**
 * Helper navigation code
 */

(function (angular, $) {

    "use strict";

    angular.module('jasifyComponents').run(function () {
        $('body').on('click', '.sub-menu > a', function (e) {
            e.preventDefault();
            $(this).next().slideToggle(200);
            $(this).parent().toggleClass('toggled');
        });
    });
    angular.module('jasifyComponents').directive('mainContent', function() {
        return {
            restrict: 'C',
            link: function(scope, element, attrs, ctrl) {
                $(element).on('click', '.main-menu a[href!=""]', function() {
                    scope.navBarController.hide();
                })
            }
        }
    })

}(angular, jQuery));