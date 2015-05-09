(function makeBookingViaJasify() {

    "use strict";

    function createBookingViaJasify() {
        var ifrm = document.createElement("IFRAME");
        ifrm.setAttribute("src", "booking-via-jasify.html");
        ifrm.setAttribute("frameBorder", "0");
        ifrm.setAttribute("width", "100%");
        ifrm.setAttribute("scrolling", "no");

        var placeholder = document.getElementById('booking-via-jasify');
        if (placeholder) {
            placeholder.appendChild(ifrm);
        }
    }

    window.createBookingViaJasify = createBookingViaJasify;

}());