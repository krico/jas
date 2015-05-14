(function makeBookingViaJasify() {

    "use strict";

    var currentOrganizationId

    function createBookingViaJasify() {

        try {
            var queryParams = {},
                scriptTagUrlQueryString = document.getElementById('booking-with-jasify-script').src.split('?')[1],
                varArray = scriptTagUrlQueryString.split("&");

            for (var i = 0; i < varArray.length; i++) {
                var param = varArray[i].split("=");
                queryParams[param[0]] = param[1];
            }

            if (currentOrganizationId === queryParams.organizationId) {
                log("Skipping rendering booking-via-jasify");
                return;
            }

            currentOrganizationId = queryParams.organizationId;

            var ifrm = document.createElement("IFRAME");
            ifrm.setAttribute("src", "booking-via-jasify.html#/" + queryParams.organizationId);
            ifrm.setAttribute("frameBorder", "0");
            ifrm.setAttribute("width", "100%");
            ifrm.setAttribute("scrolling", "no");

            var placeholder = document.getElementById('booking-via-jasify');
            if (placeholder) {
                placeholder.innerHTML = "";
                placeholder.appendChild(ifrm);
                log("Rendering booking-via-jasify");
            }
        }
        catch(error) {
            log(error);
        }
    }

    function log(message) {
        if (console && console.log) {
            console.log(message);
        }
    }

    window.createBookingViaJasify = createBookingViaJasify;

}());