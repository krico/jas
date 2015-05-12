/*global window */
(function (angular, swal) {

    "use strict;"

    angular
        .module('jasifyComponents')
        .factory('jasDialogs', jasDialogs);

    function jasDialogs() {

        return {
            success: function(message) {
                swal && swal(
                    "",
                    message,
                    "success")
            },
            warning: function(message) {
                swal && swal(
                    "",
                    message,
                    "warning")
            },
            error: function(message) {
                swal && swal(
                    "Operation Failed",
                    message,
                    "warning")
            }
        };
    }

}(window.angular, window.swal));