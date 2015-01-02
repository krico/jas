(function () {

    /**
     *  Session is a singleton that mimics the server-side session
     */
    angular.module('jasify').service('Session', session);

    function session() {

        this.create = function (sessionId, userId, admin) {
            this.id = sessionId;
            this.userId = userId;
            this.admin = admin === true;
        };

        this.destroy = function () {
            this.id = null;
            this.userId = null;
            this.admin = false;
        };

        this.destroy();

        return this;
    }
})();