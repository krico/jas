/**
 * Global variables and functions
 */


var gapi = gapi || {};
var jas = jas || {};

/**
 * This is the function called by the google api client when gapi is loaded
 */
function initializeEndpoint() {
    if (window.endpointInitialize) window.endpointInitialize();
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