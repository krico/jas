/**
 * Global variables and functions
 */


var gapi = gapi || {};
var jas = jas || {};

/**
 * This is the function called by the google api client when gapi is loaded
 */
function initializeEndpoint() {
    var attempts = 0;
    var timerRegistration = null;
    if (window.endpointInitialize) {
        window.endpointInitialize();
    } else {
        //This happens if "client.js" finishes loading before our own files.
        //In this case we have to try later...
        console.log('window.endpointInitialize not defined yet');
        waitForLoad();
    }

    function waitForLoad() {
        ++attempts;
        console.log('waitForLoad(' + attempts + ')');
        if (timerRegistration === null) {
            timerRegistration = window.setInterval(waitForLoad, 500);
            return;
        }

        if (window.endpointInitialize) {
            console.log('window.endpointInitialize is available now!');
            window.clearInterval(timerRegistration);
            window.endpointInitialize();
            return;
        }

        if (attempts > 60) {
            //After 60 seconds we give up
            window.clearInterval(timerRegistration);
            console.error('Load not yet finished after 60 attempts, giving up!');
            return;
        }
    }
}

(function (j) {

    /**
     * A function for quoting regular expressions
     * @param str the regex
     * @returns {string} the quoted regex
     */
    RegExp.quote = function (str) {
        return (str + '').replace(/([.?*+^$[\]\\(){}|-])/g, "\\$1");
    };

    /**
     * When you are not sure what an object is, this function tries to help.
     * Example $log.debug('What is this: ' + jas.debugObjext(x));
     *
     * @param o anytihng
     * @returns {string} with the debug data
     */
    j.debugObject = function jasDebugObject(o) {
        try {
            return o.toSource();
        } catch (e) {
        }
        var dbg = '';
        for (var i in o) {
            dbg += 'o.' + i + ' = "';
            try {
                dbg += o[i];
            } catch (e) {
            }
            dbg += '"\n';
        }
        return dbg;
    };
})(jas);