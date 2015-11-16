(function (angular) {
    angular.module('jasifyComponents').factory('Multipass', multipass);

    function multipass(Endpoint) {
        var Multipass = {
            add: add,
            get: get,
            query: query,
            remove: remove,
            update: update
        };

        function add(organizationId, multipass) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.add({organizationId: Endpoint.fetchId(organizationId), multipass: multipass}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.get({id: id}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.query({organizationId: Endpoint.fetchId(organizationId)}).then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.remove({id: Endpoint.fetchId(id)}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(multipass) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.update(multipass).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return Multipass;
    }
})(angular);