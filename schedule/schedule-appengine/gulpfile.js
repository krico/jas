'use strict';

var argv = require('yargs').argv,
    path = require('path'),
    gulp = require('gulp'),
    file = require('gulp-file'),
    replace = require('gulp-replace'),
    merge = require('merge-stream'),
    print = require('gulp-print'),
    gutil = require('gulp-util'),
    less = require('gulp-less'),
    sourcemaps = require('gulp-sourcemaps'),
    ngAnnotate = require('gulp-ng-annotate'),
    uglify = require('gulp-uglify'),
    jshint = require('gulp-jshint'),
    minifyCSS = require('gulp-minify-css'),
    htmlmin = require('gulp-htmlmin'),
    rename = require('gulp-rename'),
    concat = require('gulp-concat'),
    wrapper = require('gulp-wrapper'),
    del = require('del'),
    symlink = require('gulp-sym'),
    bower = require('gulp-bower'),
    karma = require('gulp-karma'),
    gulpif = require('gulp-if'),
    templateCache = require('gulp-angular-templatecache'),
    plumber = require('gulp-plumber'),
    plug = require('gulp-load-plugins')(),
    runSequence = require('run-sequence');

var bowerInstalled = false;
var paths = require('./dynamic-paths.js');

paths.cssBuild = paths.build + '/css/';
paths.jsBuild = paths.build + '/js/';
paths.appRoot = paths.build + '/../';

gutil.log('Project version: ' + gutil.colors.cyan(paths.projectVersion));

gulp.task('clean', clean);
gulp.task('sym', ['build'], sym);
gulp.task('bower-install', bowerInstall);

gulp.task('client-dependencies-css', ['bower-install'], clientDependenciesCssFun('main'));
gulp.task('client-dependencies-booking', ['bower-install'], clientDependenciesCssFun('booking'));

gulp.task('client-dependencies-js-boot', ['bower-install'], clientDependenciesJsFun('boot'));
gulp.task('client-dependencies-js-main', ['bower-install'], clientDependenciesJsFun('main'));
gulp.task('client-dependencies-js-test', ['bower-install'], clientDependenciesJsFun('test'));
gulp.task('client-dependencies-js-iframecontentwindow', ['bower-install'], clientDependenciesJsFun('iframecontentwindow'));
gulp.task('client-dependencies-js-iframeresizer', ['bower-install'], clientDependenciesJsFun('iframeresizer'));
gulp.task('client-dependencies-js',
    [
        'client-dependencies-js-boot',
        'client-dependencies-js-main',
        'client-dependencies-js-test',
        'client-dependencies-js-iframecontentwindow',
        'client-dependencies-js-iframeresizer'
    ]
);

gulp.task('client-dependencies', ['client-dependencies-js', 'client-dependencies-css', 'client-dependencies-booking']);

gulp.task('client-tpl', clientTpl);
gulp.task('client-js', clientJs);

gulp.task('client-css', ['bower-install'], clientCss);
gulp.task('booking-css', ['bower-install'], bookingCss);

gulp.task('client', ['client-tpl', 'client-js', 'client-dependencies', 'client-css', 'booking-css']);

gulp.task('lint-js', lintJs);
gulp.task('lint-test-js', lintTestJs);
gulp.task('lint', ['lint-js', 'lint-test-js']);
gulp.task('html', html);
gulp.task('static-html', staticHtml);
gulp.task('images', images);
gulp.task('test', ['build'], testClient);
gulp.task('test-prod', ['build-prod'], testClient);
gulp.task('watch', rebuild);
gulp.task('custom-js', customJs);

/**
 * Cache busting & revision tasks
 * */
gulp.task('jsRev', jsRev);
gulp.task('cssRev', cssRev);
gulp.task('jsRevReplace', ['jsRev'], jsRevReplace);
gulp.task('cssRevReplace', ['cssRev'], cssRevReplace);
gulp.task('rev', ['jsRevReplace', 'cssRevReplace']);

gulp.task('build', ['client', 'html', 'static-html', 'images', 'custom-js']);
gulp.task('build-prod', function (callback) {
    runSequence('clean', 'build', 'rev', callback);
});
gulp.task('default', ['watch', 'build', 'lint']);

function customJs() {
    return gulp
        .src(paths.customJs)
        .pipe(gulp.dest(paths.appRoot))
}

function rebuild() {

    //this is so we only lint after generating client.js (to allow us to reload on browser sooner ;-)
    gulp.task('lint-after-client-js', ['client-js'], lintJs);

    gulp.watch(paths.js, ['lint-after-client-js']);
    gulp.watch(paths.partials, ['client-tpl']);
    gulp.watch(paths.test.js, ['lint-test-js']);
    gulp.watch(paths.watchCss, ['client-css', 'booking-css']);
    gulp.watch(paths.html, ['html']);
    gulp.watch(paths.staticHtml, ['static-html']);
    gulp.watch(paths.images, ['images']);
    gulp.watch(paths.customJs, ['custom-js']);
}

function clean(cb) {
    del([paths.build, paths.symBuild], cb);
}

function sym(cb) {
    return gulp
        .src(paths.build)
        .pipe(symlink(paths.symBuild, {force: true, relative: true}));
}

function bowerInstall(cb) {
    if (bowerInstalled) {
        return cb();
    }
    bowerInstalled = true;
    return bower();
}

function clientTpl(cb) {
    return gulp.src(paths.partials)
        .pipe(plumber())
        .pipe(htmlmin({
            collapseWhitespace: true,
            removeAttributeQuotes: true,
            removeComments: true
        }))
        .pipe(templateCache({
            filename: 'jasify.tpl.js',
            module: 'jasify.templates'
        }))
        .pipe(wrapper({
            header: '(function(angular){',
            footer: '})(angular);'
        }))
        .pipe(sourcemaps.init())
        .pipe(gulp.dest(paths.jsBuild))
        .pipe(ngAnnotate())
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.jsBuild));
}

function clientJs(cb) {
    var versionInfo = {number: 'unknown', branch: 'none', timestamp: new Date().getTime(), version: '0.0-DEV'};
    if (argv.buildNumber) {
        versionInfo.number = argv.buildNumber;
        versionInfo.branch = argv.buildBranch;
        versionInfo.timestamp = argv.buildTimestamp;
        versionInfo.version = argv.buildVersion;
    }
    var vi = 'number: ' + gutil.colors.cyan(versionInfo.number) +
        ', branch: ' + gutil.colors.cyan(versionInfo.branch) +
        ', timestamp: ' + gutil.colors.cyan(versionInfo.timestamp) +
        ', version: ' + gutil.colors.cyan(versionInfo.version);

    gutil.log('Version information ' + vi);

    return gulp.src(paths.js)
        .pipe(plumber())
        .pipe(ngAnnotate())
        .pipe(sourcemaps.init())
        .pipe(replace('@NUMBER@', versionInfo.number))
        .pipe(replace('@BRANCH@', versionInfo.branch))
        .pipe(replace('@TIMESTAMP@', versionInfo.timestamp))
        .pipe(replace('@VERSION@', versionInfo.version))
        .pipe(concat('jasify.js'))
        .pipe(gulp.dest(paths.jsBuild))
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.jsBuild));
}

function clientDependenciesJsFun(key) {
    var src = [];
    for (var i = 0; i < paths.dependencies.js[key].length; ++i) {
        src.push(paths.bower + '/' + paths.dependencies.js[key][i]);
    }
    return function (cb) {
        return gulp.src(src)
            .pipe(plug.size({showFiles: true, title: key}))
            .pipe(plumber())
            .pipe(ngAnnotate())
            .pipe(sourcemaps.init())
            .pipe(concat('dep-' + key + '.js'))
            .pipe(gulp.dest(paths.jsBuild))
            .pipe(uglify())
            .pipe(rename({extname: '.min.js'}))
            .pipe(plug.size({title: key + '.min'}))
            .pipe(sourcemaps.write('./'))
            .pipe(gulp.dest(paths.jsBuild));
    };
}

function lintTestJs(cb) {
    return gulp.src(paths.test.js)
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
}

function lintJs(cb) {
    return gulp.src(paths.js)
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
}

function clientCss(cb) {
    return gulp.src(paths.css)
        .pipe(plumber())
        //.pipe(sourcemaps.init())
        .pipe(less({
            paths: [
                path.join(paths.bower, 'bootstrap', 'less')
            ]
        }))
        .pipe(concat('jasify.css'))
        .pipe(gulp.dest(paths.cssBuild))
        .pipe(minifyCSS())
        .pipe(rename({extname: '.min.css'}))
        //.pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.cssBuild));
}

function bookingCss(cb) {
    return gulp.src(paths.cssBooking)
        .pipe(plumber())
        //.pipe(sourcemaps.init())
        .pipe(less({
            paths: [
                path.join(paths.bower, 'bootstrap', 'less')
            ]
        }))
        .pipe(concat('booking.css'))
        .pipe(gulp.dest(paths.cssBuild))
        .pipe(minifyCSS())
        .pipe(rename({extname: '.min.css'}))
        //.pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.cssBuild));
}

function clientDependenciesCssFun(key) {
    var src = [];
    var fonts = [];
    for (var i = 0; i < paths.dependencies.css[key].length; ++i) {
        var items = path.join(paths.bower, paths.dependencies.css[key][i]);
        src.push(items);
        fonts.push(items.replace(/\/css\/[^\/]+$/, "/fonts/**/*.*"));
    }
    fonts.push('src/main/fonts' + '/**/*.*');
    fonts.push(path.join(paths.bower, 'ionicons', 'fonts') + '/**/*.*');
    fonts.push(path.join(paths.bower, 'bootstrap', 'fonts') + '/**/*.*');
    fonts.push(path.join(paths.bower, 'font-awesome', 'fonts') + '/**/*.*');
    return function (cb) {

        gulp.src(fonts)
            .pipe(gulp.dest(paths.build + '/fonts'));

        return gulp.src(src)
            .pipe(concat('dep-' + key + '.css'))
            .pipe(gulp.dest(paths.cssBuild))
            .pipe(minifyCSS())
            .pipe(rename({extname: '.min.css'}))
            .pipe(gulp.dest(paths.cssBuild));
    };
}

function jsRev() {
    return gulp.src(paths.jsBuild + '*.js')
        .pipe(plug.rev())
        .pipe(gulp.dest(paths.jsBuild))
        .pipe(plug.rev.manifest())
        .pipe(gulp.dest(paths.jsBuild))
}

function cssRev() {
    return gulp.src(paths.cssBuild + '*.css')
        .pipe(plug.rev())
        .pipe(gulp.dest(paths.cssBuild))
        .pipe(plug.rev.manifest())
        .pipe(gulp.dest(paths.cssBuild))
}

function jsRevReplace() {

    var manifest = gulp.src(paths.jsBuild + 'rev-manifest.json'),
        htmlFiles = paths.appRoot + '*.html';

    return gulp.src(htmlFiles)
        .pipe(replace(/build\/js\/([^\/]+)\.js/g, 'build/js/$1.min.js'))
        .pipe(plug.revReplace({manifest: manifest}))
        .pipe(gulp.dest(paths.appRoot));
}

function cssRevReplace() {

    var manifest = gulp.src(paths.cssBuild + 'rev-manifest.json'),
        htmlFiles = paths.appRoot + '*.html';

    return gulp.src(htmlFiles)
        .pipe(replace(/build\/css\/([^\/]+)\.css/g, 'build/css/$1.min.css'))
        .pipe(plug.revReplace({manifest: manifest}))
        .pipe(gulp.dest(paths.appRoot));
}

function html(cb) {
    return gulp.src(paths.html)
        .pipe(plumber())
        //.pipe(htmlmin({collapseWhitespace: true, minifyJS: true}))
        .pipe(gulp.dest(paths.appRoot))
}

function staticHtml(cb) {
    return gulp.src(paths.staticHtml)
        .pipe(plumber())
        .pipe(gulp.dest(paths.appRoot))
}

function images(cb) {
    return gulp.src(paths.images)
        .pipe(plumber())
        .pipe(gulp.dest(paths.build + '/img'))
}


function testClient(cb) {
    if (argv.skipteststrue) {
        gutil.log(gutil.colors.red('SKIPPING TESTS'));
    }
    return gulp.src([])
        .pipe(gulpif(argv.skipteststrue,
            gutil.noop(),
            karma({configFile: paths.test.karmaConfig, cmd: 'start'}
            )))
        .on('error', function (err) {
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
}
