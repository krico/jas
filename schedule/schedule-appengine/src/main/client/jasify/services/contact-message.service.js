(function (angular) {
    angular.module('jasifyComponents').factory('ContactMessage', contactMessage);

    function contactMessage(Endpoint) {
        var ContactMessage = {
            add: add,
            get: get,
            query: query,
            remove: remove
        };

        function add(message) {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.add(message).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.get({id: id}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function query() {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.query().then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.remove({id: Endpoint.fetchId(id)}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return ContactMessage;
    }
})(angular);