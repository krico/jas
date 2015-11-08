/*global window, _ */
(function (angular) {

    'use strict';

    angular.module('jasify.admin').controller('AdminContactMessageController', AdminContactMessageController);

    function AdminContactMessageController(message) {
        var vm = this;
        vm.message = message;
    }

}(window.angular));