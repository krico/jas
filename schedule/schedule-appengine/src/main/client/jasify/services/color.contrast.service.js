/*global window */
(function (angular) {

    "use strict";

    angular
        .module('jasifyComponents')
        .factory('getContrast', function () {
            return {
                compute: function getContrastYIQ(hexcolor) {

                    try {
                        hexcolor = hexcolor.substr(1);

                        var r = parseInt(hexcolor.substr(0, 2), 16);
                        var g = parseInt(hexcolor.substr(2, 2), 16);
                        var b = parseInt(hexcolor.substr(4, 2), 16);
                        var yiq = ((r * 299) + (g * 587) + (b * 114)) / 1000;
                        return (yiq >= 128) ? 'black' : 'white';
                    } catch (ex) {
                        return 'black';
                    }
                }
            };
        });
}(window.angular));