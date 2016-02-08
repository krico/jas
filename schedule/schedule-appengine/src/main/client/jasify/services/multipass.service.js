(function (angular) {
    angular.module('jasifyComponents').factory('Multipass', multipass);

    function multipass(Jasify, Endpoint) {
        var Multipass = {
            add: add,
            get: get,
            query: query,
            remove: remove,
            update: update
        };

        function add(organizationId, multipass) {
            return Jasify.multipasses.add({
                organizationId: Endpoint.fetchId(organizationId),
                multipass: multipass
            });
        }

        function get(multipassId) {
            return Jasify.multipasses.get(Endpoint.fetchId(multipassId));
        }

        function query(organizationId) {
            return Jasify.multipasses.query(Endpoint.fetchId(organizationId));
        }

        function remove(multipassId) {
            return Jasify.multipasses.remove(Endpoint.fetchId(multipassId));
        }

        function update(multipass) {
            var multipassId = Endpoint.fetchId(multipass.id);
            return Jasify.multipasses.update({
                multipassId: multipassId,
                multipass: multipass},
            multipassId);
        }

        return Multipass;
    }
})(angular);