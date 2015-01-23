'use strict';

var argv = require('yargs').argv,
    gulp = require('gulp'),
    merge = require('merge-stream'),
    print = require('gulp-print'),
    gutil = require('gulp-util'),
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
    paths = require('./paths.json');

gulp.task('clean', clean);
gulp.task('sym', ['build'], sym);
gulp.task('bower-install', bowerInstall);
gulp.task('client-tpl', clientTpl);
gulp.task('client-js', clientJs);
gulp.task('client', ['client-tpl', 'client-js']);
gulp.task('lint-js', lintJs);
gulp.task('lint-test-js', lintTestJs);
gulp.task('lint', ['lint-js', 'lint-test-js']);
gulp.task('styles', styles);
gulp.task('html', html);
gulp.task('static-html', staticHtml);
gulp.task('test', ['build'], testClient);
gulp.task('watch', rebuild);
gulp.task('build', ['client', 'styles', 'html', 'static-html', 'bower-install']);
gulp.task('default', ['watch', 'build', 'lint']);

function rebuild() {

    //this is so we only lint after generating client.js (to allow us to reload on browser sooner ;-)
    gulp.task('lint-after-client-js', ['client-js'], lintJs);

    gulp.watch(paths.js, ['lint-after-client-js']);
    gulp.watch(paths.partials, ['client-tpl']);
    gulp.watch(paths.test.js, ['lint-test-js']);
    gulp.watch(paths.css, ['styles']);
    gulp.watch(paths.html, ['html']);
    gulp.watch(paths.staticHtml, ['static-html']);
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
    return bower()
        .pipe(gulp.dest(paths.build + '/lib'));
}

function clientTpl(cb) {
    return gulp.src(paths.partials)
        .pipe(plumber())
        .pipe(htmlmin({
            collapseWhitespace: true,
            removeAttributeQuotes: true,
            removeComments: true
        }))
        .pipe(templateCache({filename: 'jasify.tpl.js'}))
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
    return gulp.src(paths.js)
        .pipe(sourcemaps.init())
        .pipe(plumber())
        .pipe(concat('jasify.js'))
        .pipe(gulp.dest(paths.build + '/js'))
        .pipe(ngAnnotate())
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/js'));
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

function styles(cb) {
    return gulp.src(paths.css)
        .pipe(sourcemaps.init())
        //.pipe(less())
        .pipe(concat('jasify.css'))
        .pipe(gulp.dest(paths.build + '/css'))
        .pipe(minifyCSS())
        .pipe(rename({extname: '.min.css'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/css'));
}


function html(cb) {
    return gulp.src(paths.html)
        .pipe(htmlmin({collapseWhitespace: true, minifyJS: true}))
        .pipe(gulp.dest(paths.build + '/../'))
}

function staticHtml(cb) {
    return gulp.src(paths.staticHtml)
        .pipe(gulp.dest(paths.build + '/../'))
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
