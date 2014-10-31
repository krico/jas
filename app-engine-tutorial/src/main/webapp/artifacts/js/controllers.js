var guestBookApp = angular.module('guestBookApp', []);

guestBookApp.controller('GuestBookCtrl', function ($scope, $http) {
        $http.get('list-greetings').success(function (data) {
            $scope.greetings = data.greetings;
            $scope.guestBook = data.guestBook;
        });

        $scope.orderProp = "-date";

        $scope.submit = function () {
            if ($scope.greeting) {
                // Simple POST request example (passing data) :
                $http.post('/sign', {content: $scope.greeting, guestbookName: 'default'}).
                    success(function (data, status, headers, config) {
                        $scope.greetings.push(data);
                    }).
                    error(function (data, status, headers, config) {
                        // called asynchronously if an error occurs
                        // or server returns response with an error status.
                        alert("Failed: " + data);
                    });
            }
        }
    }
);
var openIdApp = angular.module('openIdApp', []);

openIdApp.controller('OpenIDCtrl', function ($scope, $http, $location) {
        //$http.get('openid').success(function (data) {
        //    $scope.clientId = data.clientId;
        //    $scope.state = data.state;
        //    $scope.applicationName = data.applicationName;
        //});
        $scope.login = function () {
            var abs = $location.absUrl();
            abs = abs.substring(0, abs.lastIndexOf('/'));
            alert(abs);
        }
    }
);
