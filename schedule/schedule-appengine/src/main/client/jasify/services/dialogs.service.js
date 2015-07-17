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
    function jasDialogs($log) {

        return {
            success: function (message) {
                if (swal) {
                    swal("", message, "success");
                }
            },
            warning: function (message) {
                if (swal) {
                    swal("", message, "warning");
                }
            },
            error: function (message, onOk) {
                if (swal) {
                    if (angular.isFunction(onOk)) {
                        swal({
                            title: "Operation Failed",
                            text: message,
                            type: "error",
                            showCancelButton: false
                        }, function () {
                            onOk();
                        });
                    } else {
                        swal("Operation Failed", message, "error");
                    }
                }
            },
            resultError: function (message, result) {
                var errorDetails = [];
                if (swal) {
                    if (result) {
                        if (result.result && result.result.error && result.result.error.message) {
                            errorDetails.push(result.result.error.message);
                        } else {
                            if (result.statusText) {
                                errorDetails.push(result.statusText);
                            }
                            if (result.status) {
                                errorDetails.push(result.status);
                            }
                        }
                    }
                    $log.debug(message, result);
                    swal("Operation Failed", message + errorDetails.join(','), "warning");
                }
            },
            ruSure: function (message, onConfirm) {
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
            },
            ok: function (title, message, onOk, closeOnConfirm, type) {
                if (swal) {
                    swal({
                        title: title,
                        text: message,
                        type: type,
                        showCancelButton: false,
                        confirmButtonText: "Ok",
                        closeOnConfirm: !!closeOnConfirm
                    }, function () {
                        onOk();
                    });
                }
            }
        };
    }

}(window.angular, window.swal));