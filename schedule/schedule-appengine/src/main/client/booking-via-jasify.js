(function makeBookingViaJasify() {

    "use strict";

    function createBookingViaJasify() {

        try {
            var queryParams = {},
                scriptTagUrlQueryString = document.getElementById('booking-with-jasify-script').src.split('?')[1],
                varArray = scriptTagUrlQueryString.split("&");

            for (var i = 0; i < varArray.length; i++) {
                var param = varArray[i].split("=");
                queryParams[param[0]] = param[1];
            }

            var ifrm = document.createElement("IFRAME");
            ifrm.setAttribute("src", "booking-via-jasify.html#/" + queryParams.organizationId);
            ifrm.setAttribute("frameBorder", "0");
            ifrm.setAttribute("width", "100%");
            ifrm.setAttribute("scrolling", "no");

            var placeholder = document.getElementById('booking-via-jasify');
            if (placeholder) {
                placeholder.appendChild(ifrm);
            }
        }
        catch(error) {
            if(console && console.log) {
                console.log(error);
            }
        }
    }

    window.createBookingViaJasify = createBookingViaJasify;

}());