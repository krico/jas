module.exports = function (config, min) {
    var paths = require('../../../paths.json');
    var suffix = min ? '.min.js' : '.js';
    config.set({
        basePath: '../../..',
        frameworks: ['jasmine'],
        files: [
            paths.build + '/lib/angular/angular.js',
            paths.build + '/lib/angular-mocks/angular-mocks.js',
            paths.build + '/lib/angular-route/angular-route.js',
            paths.build + '/lib/angular-resource/angular-resource.js',
            paths.build + '/lib/angular-messages/angular-messages.js',
            paths.build + '/lib/angular-messages/angular-messages.js',
            paths.build + '/lib/angular-cookies/angular-cookies.js',
            paths.build + '/lib/angular-bootstrap/ui-bootstrap-tpls.js',
            paths.build + '/lib/spin.js/spin.js',
            paths.build + '/lib/angular-spinner/angular-spinner.min.js',
            paths.build + '/js/jasify'+suffix,
            'src/test/javascript/**/*.js'
        ],
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
