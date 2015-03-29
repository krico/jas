(function (angular) {
    angular.module('jasifyComponents').factory('ShoppingCart', shoppingCart);

    function shoppingCart(Endpoint, $q, $log) {
        var ShoppingCart = {
            get: get,
            getUserCart: getUserCart,
            removeItem: removeItem
        };

        function getUserCart() {
            return Endpoint.jasify(function (jasify) {
                return jasify.carts.getUserCart()
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

        return ShoppingCart;
    }
})(angular);