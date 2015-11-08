(function (angular) {
    angular.module('jasifyComponents').factory('ContactMessage', contactMessage);

    function contactMessage(Endpoint) {
        var ContactMessage = {
            get: get,
            query: query,
            send: send
        };

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.get({id: id})
                    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function query() {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.query().then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function send(message) {
            return Endpoint.jasify(function (jasify) {
                return jasify.contactMessages.send(message).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return ContactMessage;
    }
})(angular);