(function (angular) {
    angular.module('jasifyComponents').factory('Multipass', multipass);

    function multipass(Endpoint) {
        var Multipass = {
            query: query,
            get: get,
            add: add,
            remove: remove,
            update: update
        };

        var currentId = 1000;
        var multipassDB = [];

        function query(organizationId) {
            return Endpoint.jasify(function (jasify) {
                var result = {items: multipassDB};
                var response = {result: result, status: 200, statusText: "OK"};
                return Endpoint.itemsResultHandler(response);
                //return jasify.multipasses.query({organizationId: Endpoint.fetchId(organizationId)})
                //    .then(Endpoint.itemsResultHandler, Endpoint.rejectHandler);
            });
        }

        function get(id) {
            return Endpoint.jasify(function (jasify) {
                for (var i = 0; i < multipassDB.length; i++) {
                    if (multipassDB[i].id == id) {
                        var response = {result: angular.copy(multipassDB[i]), status: 200, statusText: "OK"};
                        return Endpoint.itemsResultHandler(response);
                    }
                }
                var rejection = {status: 400, statusText: "NOK"};
                return Endpoint.rejectHandler(rejection);
                //return jasify.multipasses.get({id: id})
                //    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function update(multipass) {
            return Endpoint.jasify(function (jasify) {
                for (var i = 0; i < multipassDB.length; i++) {
                    if (multipassDB[i].id == multipass.id) {
                        multipassDB[i] = multipass;
                        var response = {result: angular.copy(multipass), status: 200, statusText: "OK"};
                        return Endpoint.itemsResultHandler(response);
                    }
                }
                var rejection = {status: 400, statusText: "NOK"};
                return Endpoint.rejectHandler(rejection);
                //return jasify.multipasses.update(multipass)
                //    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function add(multipass) {
            return Endpoint.jasify(function (jasify) {
                var multipassToAdd = angular.copy(multipass);
                multipassToAdd.id = "MP" + currentId + "-" + multipassToAdd.organizationId;
                multipassDB.push(multipassToAdd);
                currentId++;
                var items = [multipassToAdd];
                var result = {items: items};
                var response = {result: result, status: 200, statusText: "OK"};
                return Endpoint.itemsResultHandler(response);
                //return jasify.multipasses.add({
                //    organizationId: Endpoint.fetchId(organizationId),
                //    multipass: multipass
                //}).then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        function remove(id) {
            return Endpoint.jasify(function (jasify) {
                for (var i = 0; i < multipassDB.length; i++) {
                    if (multipassDB[i].id == id) {
                        multipassDB.splice(i, 1);
                        break;
                    }
                }
                var response = {status: 200, statusText: "OK"};
                return Endpoint.itemsResultHandler(response);
                //return jasify.multipasses.remove({id: Endpoint.fetchId(id)})
                //    .then(Endpoint.resultHandler, Endpoint.rejectHandler);
            });
        }

        return Multipass;
    }
})(angular);