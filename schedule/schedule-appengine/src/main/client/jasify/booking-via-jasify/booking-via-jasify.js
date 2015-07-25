/*global window, _ */
(function (angular, _) {

    'use strict';

    angular
        .module('jasify.bookingViaJasify')
        .controller('BookingViaJasify', BookingViaJasify);

    function BookingViaJasify(AUTH_EVENTS, $scope, $log, $rootScope, $window, $timeout, $location, $filter, localStorageService,
                              sessionStorageKeys, PopupWindow, ShoppingCart, ActivityPackage, Auth, activities,
                              activityPackages, jasDialogs, getContrast, CHECKOUT_WINDOW, $cookies) {

        var vm = this;
        vm.wizardOptions = {
            onInit: onWizardInit,
            onTabClick: onWizardClick
        };
        vm.activities = activities.items;
        vm.activitySelection = [];

        vm.activityPackages = activityPackages.items;
        vm.activityPackageActivities = {};
        vm.activityPackageSelection = {};
        vm.activityPackageSelectAllFlags = [];

        var $translate = $filter('translate');

        vm.getStyle = function (activity) {
            return {
                'border-left': '1px solid ' + activity.activityType.colourTag,
                'margin-bottom': '15px'
            };
        };

        vm.getBadge = function (activity) {
            return {
                'background-color': activity.activityType.colourTag,
                'color': getContrast.compute(activity.activityType.colourTag)
            };
        };

        wireUpSessionStorage();

        angular.forEach(this.activityPackages, function (activityPackage) {
            ActivityPackage.getActivities(activityPackage.id).then(function (result) {
                vm.activityPackageActivities[activityPackage.id] = result;
                activityPackageExistsCheck(activityPackage.id);
                updateSelectAllFlags();
            });
        });

        this.auth = Auth;
        this.bookIt = bookIt;
        this.isActivityFullyBooked = isActivityFullyBooked;
        this.isActivityPackageFullyBooked = isActivityPackageFullyBooked;
        this.disableActivityPackageActivitySelection = disableActivityPackageActivitySelection;
        this.confirmRemoveActivity = confirmRemoveActivity;
        this.confirmRemoveActivityPackage = confirmRemoveActivityPackage;
        this.confirmClearActivitySelection = confirmClearActivitySelection;
        this.confirmClearPackageSelection = confirmClearPackageSelection;
        this.hasCompletedActivityPackages = hasCompletedActivityPackages;
        this.packageSelectionIncomplete = packageSelectionIncomplete;
        this.packageSelectionComplete = packageSelectionComplete;
        this.canSelectAllForActivityPackage = canSelectAllForActivityPackage;
        this.packageSelectionTooBig = packageSelectionTooBig;
        this.selectAllForActivityPackage = selectAllForActivityPackage;
        this.updateSelectAllFlag = updateSelectAllFlag;
        this.isActivitySelected = function (activity) {
            return _.find(vm.activitySelection, {'id': activity.id});
        };

        this.isSelectedActivityPackageItem = function (activity, activityPackage) {
            return vm.activityPackageSelection[activityPackage.id] &&
                _.find(vm.activityPackageSelection[activityPackage.id], {'id': activity.id});
        };

        $rootScope.$on(AUTH_EVENTS.accountCreated, function () {
            Auth.restore(true);
        });

        function onWizardClick(tab, navigation, index) {
            $timeout(function () {
                var v = activeTab();
                localStorageService.set(sessionStorageKeys.selectedTabIndex, v);
            }, 500);
        }

        function onWizardInit(tab, navigation, index) {
            $timeout(function () {
                var tabIndex = localStorageService.get(sessionStorageKeys.selectedTabIndex);
                if (angular.isNumber(tabIndex)) {
                    $('#wizard').bootstrapWizard('show', tabIndex);
                }
            });

            return true;
        }

        function activeTab() {
            if ($('#activities').css('display') != 'none') {
                return 0;
            }
            if ($('#activity-packages').css('display') != 'none') {
                return 1;
            }
            if ($('#checkout').css('display') != 'none') {
                return 2;
            }
            return -1;
        }

        function confirmRemoveActivity(activity) {
            var translation = $translate('REMOVE_ACTIVITY');
            jasDialogs.ruSure(translation, function () {
                $rootScope.$apply(function () {
                    vm.activitySelection.splice(vm.activitySelection.indexOf(activity), 1);
                });
            });
        }

        function confirmClearPackageSelection() {
            var translation = $translate('CLEAR_PACKAGE_SELECTION_PROMPT');
            jasDialogs.ruSure(translation, function () {
                $rootScope.$apply(function () {
                    vm.activityPackageSelection = {};
                });
            });
        }
        function confirmClearActivitySelection() {
            var translation = $translate('CLEAR_ACTIVITY_SELECTION_PROMPT');
            jasDialogs.ruSure(translation, function () {
                $rootScope.$apply(function () {
                    vm.activitySelection = [];
                });
            });
        }

        function confirmRemoveActivityPackage(activityPackage) {
            var translation = $translate('REMOVE_ACTIVITY_PACKAGE');
            jasDialogs.ruSure(translation, function () {
                $rootScope.$apply(function () {
                    delete vm.activityPackageSelection[activityPackage.id];
                });
            });
        }

        function disableActivityPackageActivitySelection(activity, activityPackage) {
            if (vm.activityPackageSelection[activityPackage.id] &&
                _.find(vm.activityPackageSelection[activityPackage.id], {id: activity.id})) {
                return false;
            }
            return vm.isActivityFullyBooked(activity) || vm.isActivityPackageFullyBooked(activityPackage);
        }

        function hasCompletedActivityPackages() {
            return _.some(vm.activityPackages, function (value) {
                return vm.packageSelectionComplete(value);
            });
        }

        function isActivityPackageFullyBooked(activityPackage) {
            return vm.activityPackageSelection[activityPackage.id] &&
                vm.activityPackageSelection[activityPackage.id].length === activityPackage.itemCount;
        }

        function isActivityFullyBooked(activity) {
            return activity.maxSubscriptions <= activity.subscriptionCount;
        }

        function packageSelectionIncomplete(activityPackage) {
            return vm.activityPackageSelection &&
                vm.activityPackageSelection[activityPackage.id] &&
                vm.activityPackageSelection[activityPackage.id].length !== 0 &&
                (activityPackage.itemCount > vm.activityPackageSelection[activityPackage.id].length);
        }

        function packageSelectionComplete(activityPackage) {
            return vm.activityPackageSelection && vm.activityPackageSelection[activityPackage.id] &&
                (activityPackage.itemCount === vm.activityPackageSelection[activityPackage.id].length);
        }

        function packageSelectionTooBig(activityPackage) {
            return vm.activityPackageSelection && vm.activityPackageSelection[activityPackage.id] &&
                (activityPackage.itemCount < vm.activityPackageSelection[activityPackage.id].length);
        }

        function selectAllForActivityPackage(activityPackage) {
            if (vm.activityPackageSelectAllFlags[activityPackage.id]) {
                vm.activityPackageSelection[activityPackage.id] =
                    angular.copy(vm.activityPackageActivities[activityPackage.id]);
            } else {
                delete vm.activityPackageSelection[activityPackage.id];
            }
        }

        function updateSelectAllFlag(activityPackageId) {
            if (vm.activityPackageSelection[activityPackageId] &&
                vm.activityPackageActivities[activityPackageId] &&
                vm.activityPackageSelection[activityPackageId].length === vm.activityPackageActivities[activityPackageId].length) {
                vm.activityPackageSelectAllFlags[activityPackageId] = true;
            } else {
                vm.activityPackageSelectAllFlags[activityPackageId] = false;
            }
        }

        function updateSelectAllFlags() {
            angular.forEach(vm.activityPackageActivities, function (activityPackageActivities, activityPackageId) {
                if (vm.activityPackageSelection[activityPackageId] &&
                    vm.activityPackageSelection[activityPackageId].length === activityPackageActivities.length) {
                    vm.activityPackageSelectAllFlags[activityPackageId] = true;
                } else {
                    vm.activityPackageSelectAllFlags[activityPackageId] = false;
                }
            });
        }

        function wireUpSessionStorage() {
            $scope.$watch('vm.activityPackageSelection', function (newValue) {
                localStorageService.set(sessionStorageKeys.activityPackageSelection, newValue);
            }, true);

            $scope.$watchCollection('vm.activitySelection', function (newValue) {
                localStorageService.set(sessionStorageKeys.activitySelection, newValue);
            });

            vm.activityPackageSelection = localStorageService.get(sessionStorageKeys.activityPackageSelection) || {};
            vm.activitySelection = localStorageService.get(sessionStorageKeys.activitySelection) || [];

            activityExistsCheck(vm.activities);
        }

        function activityPackageExistsCheck(activityPackageId) {

            var activityPackageSelection = vm.activityPackageSelection[activityPackageId] || [];

            _.remove(activityPackageSelection, function(activityPackageSelectedActivity) {
                return !_.find(vm.activityPackageActivities[activityPackageId], { 'id': activityPackageSelectedActivity.id });
            });

            if (activityPackageSelection.length === 0) {
                delete vm.activityPackageSelection[activityPackageId];
            }
        }

        function activityExistsCheck(activities) {
            _.remove(vm.activitySelection, function(selectedActivity) {
                return !_.find(activities, { 'id': selectedActivity.id });
            });
        }

        function activityPackageCheck() {

        }

        function canSelectAllForActivityPackage(activityPackage) {

            var numActivitiesToBook = 0;

            angular.forEach(vm.activityPackageActivities[activityPackage.id], function (activity) {
                if (isActivityFullyBooked(activity) === false) {
                    numActivitiesToBook += 1;
                }
            });

            return numActivitiesToBook === activityPackage.itemCount;
        }

        function bookIt() {
            var request = {activityIds: [], activityPackageSubscriptions: []};

            angular.forEach(vm.activitySelection, function (value) {
                $log.debug("Adding activity to shopping cart: " + value.id);
                this.activityIds.push(value.id);
            }, request);

            var completeActivityPackagesList = _.filter(vm.activityPackages, vm.packageSelectionComplete);

            angular.forEach(completeActivityPackagesList, function (value) {
                $log.debug("Adding activity package to shopping cart: " + value.id);

                var activityIds = [];
                angular.forEach(vm.activityPackageSelection[value.id], function (activity) {
                    this.push(activity.id);
                }, activityIds);

                this.activityPackageSubscriptions.push({
                    activityPackageId: value.id,
                    activityIds: activityIds
                });
            }, request);

            ShoppingCart.createAnonymousCart(request).then(confirmPopup, error);

            function error(r) {
                jasDialogs.error(r.statusText + ' (' + r.status + ')');
            }
        }

        function confirmPopup(r) {
            var translationTitle = $translate('PROCEED_IN_NEW_WINDOW');
            var translationBody = $translate('PROCEED_TO_BOOKING_VIA_JASIFY');

            jasDialogs.ok(translationTitle, translationBody, onOk);
            function onOk() {
                var w = $window.innerWidth || 820;
                var h = $window.innerHeight || 620;
                w = w - 20;
                h = h - 20;
                if (CHECKOUT_WINDOW.statusCookie in $cookies) {
                    delete $cookies[CHECKOUT_WINDOW.statusCookie];
                }
                PopupWindow.open('/checkout-window.html#/anonymous-checkout/' + r.id, {
                    width: w,
                    height: h
                }).then(onWindowClosed, function (res) {
                    jasDialogs.error(res);
                });
            }

            function onWindowClosed() {
                var status = $cookies[CHECKOUT_WINDOW.statusCookie];
                if (status) {
                    if (status == CHECKOUT_WINDOW.statusSuccess) {
                        var proceedToPaymentTitle = $translate('CHECKOUT_COMPLETE');
                        var proceedToPaymentBody = $translate('PROCEED_IN_NEW_WINDOW');
                        jasDialogs.ok(proceedToPaymentTitle, proceedToPaymentBody, function () {
                            $timeout(function () {
                                $location.path('/done');
                            }, 500);
                        }, true, 'success');
                    } else if (status == CHECKOUT_WINDOW.statusPaymentFailed) {
                        var paymentNotCompleteTranslation = $translate('CHECKOUT_PAYMENT_NOT_COMPLETE_RETRY_SELECTION');
                        jasDialogs.warning(paymentNotCompleteTranslation);
                    } else if (status == CHECKOUT_WINDOW.statusAuthenticating) {
                        var authenticationtNotCompleteTranslation = $translate('CHECKOUT_AUTHENTICATION_NOT_COMPLETE_RETRY_SELETION');
                        jasDialogs.warning(authenticationtNotCompleteTranslation);
                    } else if (status == CHECKOUT_WINDOW.statusCheckout) {
                        var checkoutNotCompleteTranslation = $translate('CHECKOUT_NOT_COMPLETE_RETRY_SELECTION');
                        jasDialogs.warning(checkoutNotCompleteTranslation);
                    } else {
                        var checkoutErrorWithStatusTranslation = $translate('CHECKOUT_ERROR');
                        jasDialogs.warning(checkoutErrorWithStatusTranslation + "(" + status + ")");
                    }
                } else {
                    //TODO: check with server
                    var checkoutErrorWithoutStatusTranslation = $translate('CHECKOUT_ERROR');
                    jasDialogs.warning(checkoutErrorWithoutStatusTranslation);
                }
            }
        }
    }
}(window.angular, _));