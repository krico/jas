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
    plumber = require('gulp-plumber');

var bowerInstalled = false;
var paths = require('./dynamic-paths.js');
gutil.log('Project version: ' + gutil.colors.cyan(paths.projectVersion));

gulp.task('clean', clean);
gulp.task('sym', ['build'], sym);
gulp.task('bower-install', bowerInstall);

gulp.task('client-dependencies-css', ['bower-install'], clientDependenciesCssFun('main'));

gulp.task('client-dependencies-js-boot', ['bower-install'], clientDependenciesJsFun('boot'));
gulp.task('client-dependencies-js-main', ['bower-install'], clientDependenciesJsFun('main'));
gulp.task('client-dependencies-js-test', ['bower-install'], clientDependenciesJsFun('test'));
gulp.task('client-dependencies-js', ['client-dependencies-js-boot', 'client-dependencies-js-main', 'client-dependencies-js-test']);

gulp.task('client-dependencies', ['client-dependencies-js', 'client-dependencies-css']);

gulp.task('client-tpl', clientTpl);
gulp.task('client-js', clientJs);

gulp.task('client-css', ['bower-install'], clientCss);
gulp.task('booking-css', ['bower-install'], bookingCss)

gulp.task('client', ['client-tpl', 'client-js', 'client-dependencies', 'client-css', 'booking-css']);

gulp.task('lint-js', lintJs);
gulp.task('lint-test-js', lintTestJs);
gulp.task('lint', ['lint-js', 'lint-test-js']);
gulp.task('html', html);
gulp.task('static-html', staticHtml);
gulp.task('images', images);
gulp.task('test', ['build'], testClient);
gulp.task('watch', rebuild);
gulp.task('custom-js', customJs);
gulp.task('build', ['client', 'html', 'static-html', 'images', 'custom-js']);
gulp.task('default', ['watch', 'build', 'lint']);

function customJs() {
    return gulp
        .src(paths.customJs)
        .pipe(gulp.dest(paths.build + '/../'))
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
    if(bowerInstalled) {return cb();}
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
        .pipe(gulp.dest(paths.build + '/js'))
        .pipe(ngAnnotate())
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/js'));
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
        .pipe(gulp.dest(paths.build + '/js'))
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/js'));
}

function clientDependenciesJsFun(key) {
    var src = [];
    for (var i = 0; i < paths.dependencies.js[key].length; ++i) {
        src.push(paths.bower + '/' + paths.dependencies.js[key][i]);
    }
    return function (cb) {
        return gulp.src(src)
            .pipe(plumber())
            .pipe(ngAnnotate())
            .pipe(sourcemaps.init())
            .pipe(concat('dep-' + key + '.js'))
            .pipe(gulp.dest(paths.build + '/js'))
            .pipe(uglify())
            .pipe(rename({extname: '.min.js'}))
            .pipe(sourcemaps.write('./'))
            .pipe(gulp.dest(paths.build + '/js'));
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
        .pipe(gulp.dest(paths.build + '/css'));
        //.pipe(minifyCSS())
        //.pipe(rename({extname: '.min.css'}))
        //.pipe(sourcemaps.write('./'))
        //.pipe(gulp.dest(paths.build + '/css'));
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
        .pipe(gulp.dest(paths.build + '/css'));
        //.pipe(minifyCSS())
        //.pipe(rename({extname: '.min.css'}))
        //.pipe(sourcemaps.write('./'))
        //.pipe(gulp.dest(paths.build + '/css'));
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
    return function (cb) {

        gulp.src(fonts)
            .pipe(gulp.dest(paths.build + '/fonts'));

        return gulp.src(src)
            .pipe(concat('dep-' + key + '.css'))
            .pipe(gulp.dest(paths.build + '/css'))
            .pipe(minifyCSS())
            .pipe(rename({extname: '.min.css'}))
            .pipe(gulp.dest(paths.build + '/css'));
    };
}


function html(cb) {
    return gulp.src(paths.html)
        .pipe(plumber())
        //.pipe(htmlmin({collapseWhitespace: true, minifyJS: true}))
        .pipe(gulp.dest(paths.build + '/../'))
}

function staticHtml(cb) {
    return gulp.src(paths.staticHtml)
        .pipe(plumber())
        .pipe(gulp.dest(paths.build + '/../'))
}

function images(cb) {
    return gulp.src(paths.images)
        .pipe(plumber())
        .pipe(gulp.dest(paths.build + '/../img'))
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
