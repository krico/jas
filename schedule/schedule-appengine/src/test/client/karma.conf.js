module.exports = function (config, min) {
    var paths = require('../../../paths.json');
    var files = [
        paths.build + '/lib/angular/angular.js',
        paths.build + '/lib/angular-mocks/angular-mocks.js',
        paths.build + '/lib/angular-route/angular-route.js',
        paths.build + '/lib/angular-resource/angular-resource.js',
        paths.build + '/lib/angular-messages/angular-messages.js',
        paths.build + '/lib/angular-messages/angular-messages.js',
        paths.build + '/lib/angular-cookies/angular-cookies.js',
        paths.build + '/lib/angular-bootstrap/ui-bootstrap-tpls.js',
        paths.build + '/lib/ui-bootstrap-datetime-picker/dist/datetime-picker.min.js',
        paths.build + '/lib/spin.js/spin.js',
        paths.build + '/lib/angular-spinner/angular-spinner.js'

    ];
    if (min) {
        files.push(paths.build + '/js/jasify.min.js');
    } else {
        files = files.concat(paths.js);
    }
    files.push('src/test/client/**/*.js');
    config.set({
        basePath: '../../..',
        frameworks: ['jasmine'],
        files: files,
        exclude: ['src/test/javascript/karma.conf*.js'],
        preprocessors: {},
        reporters: ['progress', 'html'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: true,
        browsers: ['Chrome'],
        singleRun: false
        //plugins: [
        //    'karma-jasmine',
        //    'karma-chrome-launcher',
        //    'karma-chrome-launcher',
        //    'karma-phantomjs-launcher'
        //]
    });
};
