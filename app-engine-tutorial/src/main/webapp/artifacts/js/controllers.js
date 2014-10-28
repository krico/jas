var guestBookApp = angular.module('guestBookApp', []);

guestBookApp.controller('GuestBookCtrl', function ($scope) {
    $scope.guestBook = 'default';
    $scope.greetings = [
        {
            'content': 'Hi there',
            'user': 'krico'
        },
        {
            'content': 'Hi again',
            'user': 'krico'
        },
        {
            'content': 'Hi from me!',
            'user': 'wszarmach'
        }
    ];
});