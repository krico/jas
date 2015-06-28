(function (angular) {
    angular.module('jasifyComponents').factory('ShoppingCart', shoppingCart);

    function shoppingCart(Endpoint, $q, $log) {
        var ShoppingCart = {
            get: get,
            createAnonymousCart: createAnonymousCart,
            anonymousCartToUserCart: anonymousCartToUserCart,
            getUserCart: getUserCart,
            clearUserCart: clearUserCart,
            addUserActivity: addUserActivity,
            addUserActivityPackage: addUserActivityPackage,
            removeItem: removeItem,
            getItem: getItem
        };

        function getUserCart() {
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.getUserCart()
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function clearUserCart() {
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.clearUserCart()
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            var req = {};
            if (id) req.id = id;

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.get(req)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function createAnonymousCart(request) {
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.createAnonymousCart(request)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function anonymousCartToUserCart(idOrCart) {
            var request = {id: fetchId(idOrCart)};
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.anonymousCartToUserCart(request)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function removeItem(cart, item) {
            var req = {
                cartId: fetchId(cart),
                ordinal: fetchId(item)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.removeItem(req)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function getItem(cart, item) {
            var req = {
                cartId: fetchId(cart),
                ordinal: fetchId(item)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.getItem(req)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function addUserActivity(activity) {
            var req = {
                activityId: fetchId(activity)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.addUserActivity(req)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function addUserActivityPackage(activityPackage, activities) {
            var req = {
                activityPackageId: fetchId(activityPackage),
                activityIds: fetchIds(activities)
            };
            $log.debug(angular.toJson(req));

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.addUserActivityPackage(req)
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function fetchId(o) {
            if (angular.isObject(o)) {
                if (!angular.isUndefined(o.ordinal))
                    return o.ordinal;
                return o.id;
            }
            return o;
        }

        function fetchIds(arr) {
            if (angular.isArray(arr)) {
                var ret = [];
                angular.forEach(arr, function (value, key) {
                    ret.push(fetchId(value));
                });
                return ret;
            } else {
                return [fetchId(arr)];
            }
        }

        return ShoppingCart;
    }
})(angular);