/*global window */
(function (angular, swal) {

    "use strict";

    angular
        .module('jasifyComponents')
        .factory('jasDialogs', jasDialogs);

    /**
     * Jasify dialogs wrapper, if we don't like swal we can change it.
     *
     * TODO: Replace if(swal) statements when we have it properly mocked
     *
     * @returns {{success: Function, warning: Function, error: Function}}
     */
    function jasDialogs() {

        return {
            success: function(message) {
                if (swal) {
                    swal("", message, "success");
                }
            },
            warning: function(message) {
                if (swal) {
                    swal("", message, "warning");
                }
            },
            error: function(message) {
                if (swal) {
                    swal("Operation Failed", message,"warning");
                }
            },
            ruSure: function(message, onConfirm) {
                if (swal) {
                    swal({
                        title: "Are you sure?",
                        text: message,
                        showCancelButton: true,
                        confirmButtonClass: "btn-danger",
                        confirmButtonText: "Yes",
                        closeOnConfirm: true
                    }, function () {

                        onConfirm();
                    });
                }
            }
        };
    }

}(window.angular, window.swal));