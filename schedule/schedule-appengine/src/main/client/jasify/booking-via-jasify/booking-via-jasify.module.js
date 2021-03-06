/*global window */
(function (angular, _, moment) {

    'use strict';

    var bookingViaJasify = angular.module('jasify.bookingViaJasify', [
        'ngRoute',
        'ngResource',
        'ngMessages',
        'ngCookies',
        'ngSanitize',
        'ui.bootstrap',
        'angularSpinner',
        'LocalStorageModule',
        'ui.bootstrap.datetimepicker',
        'pascalprecht.translate',
        'jasifyComponents',
        'jasify.authenticate',
        'jasify.payment',
        'jasify.balance',
        'jasify.checkout',
        'jasify.templates',
        "checklist-model",
        "jasify.filters"
    ]);


    bookingViaJasify.run(function ($rootScope, $modal, AUTH_EVENTS) {

        var modalInstance,
            scope = $rootScope.$new();

        scope.ok = function () {
            modalInstance.close();
        };

        $rootScope.$on(AUTH_EVENTS.loginFailed, function () {
            modalInstance = $modal.open({
                templateUrl: 'booking-via-jasify/loginFailed.html',
                size: 'sm',
                scope: scope
            });
        });
    });

    bookingViaJasify.config(bookingViaRoutes);
    bookingViaJasify.constant('sessionStorageKeys', {
        activityPackageSelection: 'activityPackageSelection',
        activitySelection: 'activitySelection',
        selectedTabIndex: 'selectedTabIndex'
    });

    function bookingViaRoutes($routeProvider) {
        $routeProvider
            .when('/done', {
                templateUrl: 'booking-via-jasify/booking-via-jasify-done.html',
                resolve: {
                    allow: /*@ngInject*/ function (Allow, localStorageService, sessionStorageKeys) {
                        localStorageService.remove(sessionStorageKeys.activityPackageSelection);
                        localStorageService.remove(sessionStorageKeys.activitySelection);
                        localStorageService.remove(sessionStorageKeys.selectedTabIndex);
                        return Allow.all();
                    }
                }
            })
            .when('/:organizationId', {
                templateUrl: 'booking-via-jasify/booking-via-jasify.html',
                controller: 'BookingViaJasify',
                controllerAs: 'vm',
                resolve: {
                    allow: function (Allow) {
                        return Allow.all();
                    },
                    activities: function ($route, Activity) {
                        return Activity.query({
                            fromDate: new Date().toISOString(),
                            organizationId: $route.current.params.organizationId
                        });
                    },
                    activityPackages: function ($q, $route, ActivityPackage) {

                        if ($route.current.params.organizationId) {
                            var dfd = $q.defer();
                            ActivityPackage.query($route.current.params.organizationId).then(function (result) {
                                result.items = _.filter(result.items, function (item) {
                                    return !item.validUntil || moment().isBefore(item.validUntil);
                                });
                                dfd.resolve(result);
                            });
                            return dfd.promise;
                        }

                        return {items: []};
                    }
                }
            });
    }

    bookingViaJasify.config(function ($translateProvider) {
        $translateProvider.useStaticFilesLoader({
            prefix: '/build/i18n/locale-',
            suffix: '.json'
        });

        //  $translateProvider.useSanitizeValueStrategy('sanitize');

        $translateProvider.registerAvailableLanguageKeys(['en', 'de'], {
            'de_CH': 'de',
            'de_DE': 'de',
            'en_GB': 'en',
            'en_US': 'en'
        }).determinePreferredLanguage().fallbackLanguage('en');
    });


    bookingViaJasify.config(function (localStorageServiceProvider, CheckoutProvider) {
        //TODO: We use storage to communicate with checkout, so needs to be local and prefix jasify
        localStorageServiceProvider
            //.setStorageType('sessionStorage')
            .setPrefix('Jasify');
        // CheckoutProvider.popupMode(true);
    });

    bookingViaJasify.directive('togglePackage', function() {
    
      var settings = {  
        animationDuration: 400
      };
      
      return {
        restrict: 'A',
        link: function(scope, element) {
          
          $(element).find('.lv-body').hide();
          
          $(element).find('.lv-header-alt').click(function(event) {
            if ($(event.target).closest('.checkbox').length === 1) {
              return;
            }
            if ($(element).hasClass('active')) {
              $(element).removeClass('active');
              $(element).find('.lv-body').slideUp(settings.animationDuration);
            } else {
              $(element).addClass('active');
              $(element).find('.lv-body').slideDown(settings.animationDuration);
            }
          });        
        }
      };
    });
  
}(window.angular, window._, window.moment));
