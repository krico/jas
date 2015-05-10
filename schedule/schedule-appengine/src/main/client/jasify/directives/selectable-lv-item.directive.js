(function (angular) {

    angular.module('jasifyComponents').directive('selectableLvItem', selectableLvItem);

    function selectableLvItem() {

        function clickHandler(event) {
            var checkboxToClick = $(this).find('input[type="checkbox"]')[0];
            if (event.target === checkboxToClick) {
                return true;
            } else {
                angular.element(checkboxToClick).click();
            }
        }

        return {
            restrict: 'C',
            link: function (scope, elem, attrs, ctrl) {
                elem.on('click', clickHandler);
            }
        };
    }
})(angular);