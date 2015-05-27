/*global window */

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

    angular.module('jasifyComponents').directive('mainMenu', function () {
        return {
            restrict: 'C',
            link: function (scope, element) {
                $(element).on('click', 'a[href!=""]', function () {
                    scope.navBarController.hide();
                });
            }
        };
    });

}(window.angular, window.jQuery));