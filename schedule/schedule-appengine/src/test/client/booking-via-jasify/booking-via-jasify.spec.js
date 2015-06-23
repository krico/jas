/*global describe, beforeEach, it, module, inject, spyOn */

describe('BookingViaJasify', function () {

    'use strict';

    var $q,
        $location,
        $rootScope,
        Auth,
        BrowserData,
        ShoopingCart,
        AUTH_EVENTS,
        controller,
        activityPackages = {items: []},
        activities = {
            "items": [{
                "id": "A159-O53",
                "start": "2015-05-07T23:00:00.000+02:00",
                "finish": "2015-05-08T00:00:00.000+02:00",
                "price": 10.0,
                "activityType": {
                    "id": "AT157-O53",
                    "name": "yoga",
                    "organizationId": "O53",
                    "price": 10.0,
                    "currency": "CHF",
                    "maxSubscriptions": 10
                },
                "currency": "CHF",
                "location": "some location",
                "maxSubscriptions": 10,
                "subscriptionCount": 10,
                "bookItUrl": "https://jasify-schedule.appspot.com/book-it.html#/A159-O53"
            }, {
                "id": "A160-O53",
                "start": "2015-05-08T20:00:00.000+02:00",
                "finish": "2015-05-08T21:00:00.000+02:00",
                "price": 10.0,
                "activityType": {
                    "id": "AT157-O53",
                    "name": "yoga",
                    "organizationId": "O53",
                    "price": 10.0,
                    "currency": "CHF",
                    "maxSubscriptions": 10
                },
                "currency": "CHF",
                "maxSubscriptions": 10,
                "subscriptionCount": 2,
                "bookItUrl": "https://jasify-schedule.appspot.com/book-it.html#/A160-O53"
            }, {
                "id": "A161-O53",
                "start": "2015-05-09T20:00:00.000+02:00",
                "finish": "2015-05-09T21:00:00.000+02:00",
                "price": 10.0,
                "activityType": {
                    "id": "AT157-O53",
                    "name": "yoga",
                    "organizationId": "O53",
                    "price": 10.0,
                    "currency": "CHF",
                    "maxSubscriptions": 10
                },
                "currency": "CHF",
                "maxSubscriptions": 10,
                "subscriptionCount": 2,
                "bookItUrl": "https://jasify-schedule.appspot.com/book-it.html#/A161-O53"
            }]
        };

    beforeEach(module('jasify.bookingViaJasify'));

    beforeEach(inject(function (_$location_, _$rootScope_, _$q_, $controller, _ShoppingCart_, _BrowserData_, _Auth_, _AUTH_EVENTS_) {
        $q = _$q_;
        $location = _$location_;
        $rootScope = _$rootScope_;
        ShoopingCart = _ShoppingCart_;
        BrowserData = _BrowserData_;
        Auth = _Auth_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        spyOn(ShoopingCart, 'createAnonymousCart').and.callFake(function () {
            return $q.when(true);
        });
        controller = $controller('BookingViaJasify', {
            $scope: $rootScope.$new(),
            activities: activities,
            activityPackages: activityPackages
        });
    }));

    it('should expose activities', function () {
        expect(controller.activities).toEqual(activities.items);
    });

    it('should recognize activity as fully booked', function () {
        expect(controller.isActivityFullyBooked(activities.items[0])).toBe(true);
    });

    it('should recognize activity as not fully booked', function () {
        expect(controller.isActivityFullyBooked(activities.items[1])).toBe(false);
    });

    it('should create anonymous cart with selection when booking', function () {
        controller.activitySelection = [{id: 'A123-O321'}];
        var req = {activityIds: [controller.activitySelection[0].id], activityPackageSubscriptions: []}

        controller.bookIt();
        expect(ShoopingCart.createAnonymousCart).toHaveBeenCalledWith(req);
    });

});