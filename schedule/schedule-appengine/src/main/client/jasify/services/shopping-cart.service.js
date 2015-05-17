(function (angular) {
    angular.module('jasifyComponents').factory('ShoppingCart', shoppingCart);

    function shoppingCart(Endpoint, $q, $log) {
        var ShoppingCart = {
            get: get,
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
                    .then(resultHandler, errorHandler);
            });
        }

        function clearUserCart() {
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.clearUserCart()
                    .then(resultHandler, errorHandler);
            });
        }

        function get(id) {
            var req = {};
            if (id) req.id = id;

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.get(req)
                    .then(resultHandler, errorHandler);
            });
        }

        function removeItem(cart, item) {
            var req = {
                cartId: fetchId(cart),
                ordinal: fetchId(item)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.removeItem(req)
                    .then(resultHandler, errorHandler);
            });
        }

        function getItem(cart, item) {
            var req = {
                cartId: fetchId(cart),
                ordinal: fetchId(item)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.getItem(req)
                    .then(resultHandler, errorHandler);
            });
        }

        function addUserActivity(activity) {
            var req = {
                activityId: fetchId(activity)
            };

            return Endpoint.jasify(function (jasify) {
                return jasify.carts.addUserActivity(req)
                    .then(resultHandler, errorHandler);
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
                    .then(resultHandler, errorHandler);
            });
        }

        function errorHandler(e) {
            return $q.reject(e);
        }

        function resultHandler(resp) {
            return resp.result;
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