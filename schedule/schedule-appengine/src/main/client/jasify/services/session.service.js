(function (angular) {

    /**
     *  Session is a singleton that mimics the server-side session
     */
    angular.module('jasifyComponents').service('Session', session);

    function session() {

        this.create = function (sessionId, userId, admin, orgMember) {
            this.id = sessionId;
            this.userId = userId;
            this.admin = admin === true;
            this.orgMember = orgMember === true;
        };

        this.destroy = function () {
            this.id = null;
            this.userId = null;
            this.admin = false;
            this.orgMember = false;
        };

        this.destroy();

        return this;
    }
})(angular);