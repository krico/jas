module.exports = function (config, min) {
    var paths = require('../../../paths.json');
    var files = [
        paths.build + '/js/dep-boot.js',
        paths.build + '/js/dep-main.js',
        paths.build + '/js/dep-test.js'
    ];
    if (min) {
        files.push(paths.build + '/js/jasify.min.js');
        files.push(paths.build + '/js/jasify.tpl.min.js');
    } else {
        files = files.concat(paths.js);
        files.push(paths.build + '/js/jasify.tpl.js');
    }

    files.push('src/test/client/**/*.js');
    config.set({
        basePath: '../../..',
        frameworks: ['jasmine'],
        files: files,
        exclude: ['src/test/client/karma.conf*.js'],
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
