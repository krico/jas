describe('BookingViaJasify', function () {

    var $q,
        $location,
        $rootScope,
        Auth,
        ShoopingCart,
        AUTH_EVENTS,
        scope,
        controller,
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
    }

    beforeEach(module('jasify.bookingViaJasify'));

    beforeEach(inject(function (_$location_, _$rootScope_, _$q_, $controller, _ShoppingCart_, _BrowserData_, _Auth_, _AUTH_EVENTS_) {
        $q = _$q_;
        $location = _$location_;
        $rootScope = _$rootScope_;
        ShoopingCart = _ShoppingCart_;
        BrowserData = _BrowserData_;
        Auth = _Auth_;
        AUTH_EVENTS = _AUTH_EVENTS_;
        spyOn(ShoopingCart, 'clearUserCart').and.callFake(function() {
            return $q.when(true);
        });
        controller = $controller('BookingViaJasify', { activities: activities});
    }));

    it('should expose activities', function () {
        expect(controller.activities).toEqual(activities.items);
    })

    it('should recognize activity as fully booked', function () {
        expect(controller.isFullyBooked(activities.items[0])).toBe(true);
    })

    it('should recognize activity as not fully booked', function () {
        expect(controller.isFullyBooked(activities.items[1])).toBe(false);
    })

    it('should clean shopping cart before booking', function() {

        controller.bookIt();

        expect(ShoopingCart.clearUserCart).toHaveBeenCalled();
    })

    it('should add selection to ShoppingCart', function() {
        controller.selection = controller.activities;
        controller.bookIt();

        var activitiesAddedToCart = [];

        spyOn(ShoopingCart, 'addUserActivity').and.callFake(function(activity) {
            activitiesAddedToCart.push(activity);
        });

        $rootScope.$apply();

        expect(activitiesAddedToCart).toEqual(controller.selection.map(function(activity) { return activity.id }));
    })

    it('should redirect to success page when all activities were booked', function() {
        controller.selection = controller.activities;
        controller.bookIt();
        spyOn(BrowserData, 'setPaymentAcceptRedirect');
        spyOn($location, 'path');

        spyOn(ShoopingCart, 'addUserActivity').and.callFake(function() {
            var dfd = $q.defer();
            dfd.resolve();
            return dfd.promise;
        });
        $rootScope.$apply();

        expect(BrowserData.setPaymentAcceptRedirect).toHaveBeenCalledWith('done');
        expect($location.path).toHaveBeenCalledWith('/checkout');
    })

    it('should redirect to success page when all activities were booked', function() {
        controller.selection = controller.activities;
        controller.bookIt();
        spyOn(BrowserData, 'setPaymentAcceptRedirect');
        spyOn(ShoopingCart, 'addUserActivity').and.callFake(function() {
            var dfd = $q.defer();
            dfd.resolve();
            return dfd.promise;
        });
        $rootScope.$apply();

        expect(BrowserData.setPaymentAcceptRedirect).toHaveBeenCalled();
    })

    it('should restore session after user account was created', function() {
        spyOn(Auth, 'restore');
        $rootScope.$broadcast(AUTH_EVENTS.accountCreated);
        $rootScope.$apply();
        expect(Auth.restore).toHaveBeenCalledWith(true);
    })

})