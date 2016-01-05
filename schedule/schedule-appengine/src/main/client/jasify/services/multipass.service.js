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
                var request = {organizationId: Endpoint.fetchId(organizationId), multipass: multipass};
                return jasify.multipasses.add(request).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(multipassId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.get({multipassId: multipassId}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.query({organizationId: Endpoint.fetchId(organizationId)}).then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(multipassId) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.remove({multipassId: Endpoint.fetchId(multipassId)}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(multipass) {
            return Endpoint.jasify(function (jasify) {
                return jasify.multipasses.update({multipassId: multipass.id, multipass: multipass}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return Multipass;
    }
})(angular);