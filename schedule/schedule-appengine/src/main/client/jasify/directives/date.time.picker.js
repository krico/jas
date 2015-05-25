/*global window */
(function (angular, $, moment) {
    'use strict';

    var setValue, getValue,
        bs3dp4ng = angular.module('bs3dp4ng', []);

    bs3dp4ng.directive('timePicker', function () {

        var defaultOptions = {
                format: 'LT',
                stepping: 15,
                locale: moment().locale()
            },
            placeholder = 'HH:MM';

        return {
            restrict: 'C',
            require: 'ngModel',
            /**
             *
             * @param $scope
             * @param element
             * @param {Object} attr - Element attributes
             * @param {string} attr.timePickerOptions - Directive parameters
             * @param {string} attr.ngModel - Model
             * @param ngModel
             */
            link: function ($scope, element, attr, ngModel) {

                var externalOptions = $scope.$eval(attr.timePickerOptions) || {},
                    timePickerOptions = angular.extend({}, defaultOptions, externalOptions),
                    timePicker = $(element).datetimepicker(timePickerOptions);

                timePicker.attr('placeholder', placeholder);

                timePicker.on('dp.change', function (e) {
                    if (e.date) {

                        var hour = e.date.get('hour'),
                            minute = e.date.get('minute'),
                            currentTime = getValue($scope, attr.ngModel),
                            newTime = {
                                hour: hour,
                                minute: minute
                            };

                        if (currentTime &&
                            currentTime.minute === newTime.minute &&
                            currentTime.hour === newTime.hour) {
                            return;
                        }

                        if (!$scope.$$phase) {
                            $scope.$apply(function () {
                                setValue($scope, attr.ngModel, newTime);
                            });
                        } else {
                            setValue($scope, attr.ngModel, newTime);
                        }
                    }
                });

                timePicker.on('click', function () {
                    timePicker.data('DateTimePicker').show();
                });

                ngModel.$parsers.push(function (viewValue) {
                    if (viewValue === '') {
                        ngModel.$setValidity('datetime', true);
                        return undefined;
                    }

                    var datetime = moment(viewValue, timePickerOptions.format);

                    if (datetime.isValid()) {
                        ngModel.$setValidity('datetime', true);
                        return datetime.format(timePickerOptions.format);
                    }

                    ngModel.$setValidity('datetime', false);
                    return undefined;
                });

                ngModel.$formatters.push(function (newValue) {
                    if (newValue) {
                        var newDate = moment();
                        newDate.set('hour', newValue.hour);
                        newDate.set('minute', newValue.minute);
                        timePicker.data('DateTimePicker').date(newDate);
                        return newDate.format(timePickerOptions.format);
                    }
                    return '';
                });
            }
        };
    });

    bs3dp4ng.directive('datePicker', function () {

        var defaultOptions = {
                format: 'L',
                showClear: true,
                showTodayButton: true,
                locale: moment().locale()
            },
            isoFormat = 'YYYY-MM-DDTHH:mm:ss';

        return {
            restrict: 'C',
            require: 'ngModel',
            /**
             *
             * @param $scope
             * @param element
             * @param {Object} attr - Element attributes
             * @param {string} attr.datePickerOptions - Directive parameters
             * @param {string} attr.ngModel - Model
             * @param ngModel
             */
            link: function ($scope, element, attr, ngModel) {

                var externalOptions = $scope.$eval(attr.datePickerOptions) || {},
                    datePickerOptions = angular.extend({}, defaultOptions, externalOptions),
                    datePicker = $(element).datetimepicker(datePickerOptions);

                try {
                    datePicker.attr('placeholder', moment.localeData()._longDateFormat.L);
                } catch(ex) {
                    console && console.log && console.log(ex);
                }

                datePicker.on('dp.change', function (e) {
                    if (e.date) {
                        if (!$scope.$$phase) {
                            $scope.$apply(function () {
                                setValue($scope, attr.ngModel, e.date.format());
                            });
                        } else {
                            setValue($scope, attr.ngModel, e.date.format());
                        }
                    }
                });

                datePicker.on('click', function () {
                    datePicker.data('DateTimePicker').show();
                });

                $scope.$watch(attr.datePickerOptions + ".minDate", function (newValue, oldValue) {

                    var picker = datePicker.data('DateTimePicker'),
                        newMinDate = moment(newValue);

                    if (!newValue || !newMinDate.isValid()) {
                        picker.minDate(false);
                        return;
                    }

                    newMinDate.set('hour', 0);
                    newMinDate.set('minute', 0);
                    newMinDate.set('second', 0);

                    if (newMinDate.format() === moment(oldValue).format()) {
                        return;
                    }

                    picker.minDate(newMinDate);
                    if (picker.date() &&
                        picker.date() < picker.minDate()) {
                        picker.date(picker.minDate());
                    }
                });

                ngModel.$parsers.push(function (viewValue) {
                    if (viewValue === '') {
                        ngModel.$setValidity('datetime', true);
                        return undefined;
                    }

                    var datetime = moment(viewValue, datePickerOptions.format);

                    if (datetime.isValid()) {
                        ngModel.$setValidity('datetime', true);
                        return datetime.format(isoFormat);
                    }

                    ngModel.$setValidity('datetime', false);
                    return undefined;
                });

                ngModel.$formatters.push(function (value) {
                    if (value) {
                        var newDate = moment(value);
                        datePicker.data('DateTimePicker').date(newDate);
                        return newDate.format('L');
                    }
                    return value;
                });
            }
        };
    });

    setValue = function (object, path, value) {
        var a = path.split('.'), o = object, i, n;
        for (i = 0; i < a.length - 1; i += 1) {
            n = a[i];
            //noinspection JSLint
            if (n in o) {
                o = o[n];
            } else {
                o[n] = {};
                o = o[n];
            }
        }
        o[a[a.length - 1]] = value;
    };

    getValue = function (object, path) {
        var o = object, n, a;
        path = path.replace(/\[(\w+)\]/g, '.$1');
        path = path.replace(/^\./, '');
        a = path.split('.');
        while (a.length) {
            n = a.shift();
            if (n in o) {
                o = o[n];
            } else {
                return;
            }
        }
        return o;
    };

}(window.angular, window.jQuery, window.moment));
