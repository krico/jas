(function (angular) {

    angular.module('jasify.bookIt').controller('BookItSubscribeController', BookItSubscribeController);

    function BookItSubscribeController(activity) {
        var vm = this;
        vm.activity = activity;
        vm.bookIt = bookIt;

        function bookIt() {
            alert('TODO: book it!');
        }
    }
})(angular);