/**
 * Created by krico on 04/11/14.
 *
 * Enables backend-less development...
 * You need to open index.html?nobackend to get this
 */

(function (ng) {

    if (!document.URL.match(/\?nobackend(#.*)?$/)) {
        return; //standard prod
    }

    console.log('======== CUIDADO!!! USING STUBBED BACKEND ========');
    initializeStubbedBackend();

    function initializeStubbedBackend() {
        // decorate http with an 2e2 mock
        ng.module('jasifyScheduleApp')
            .config(function ($provide) {
                $provide.decorator('$httpBackend', angular.mock.e2e.$httpBackendDecorator);
            })
            .run(BackendMock);

        /**
         * This is our backend replacement for backend-less dev :-)
         *
         * @param $httpBackend
         * @constructor
         */
        function BackendMock($httpBackend) {

            //Pass through so that gets to our partials work
            $httpBackend.whenGET(/^(\/)?views\/.*\.html$/).passThrough();
        }
    }
})(angular);

