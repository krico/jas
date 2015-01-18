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
    paths = require('./paths.json');

gulp.task('clean', clean);
gulp.task('sym', ['build'], sym);
gulp.task('bower-install', bowerInstall);
gulp.task('client', client);
gulp.task('test-client-js-hint', testClientJsHint);
gulp.task('styles', styles);
gulp.task('html', html);
gulp.task('test', ['build'], testClient);
gulp.task('watch', rebuild);
gulp.task('build', ['client', 'styles', 'html', 'bower-install']);
gulp.task('default', ['watch', 'build', 'test-client-js-hint']);

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

function client(cb) {
    var templates = gulp.src(paths.partials)
        .pipe(htmlmin({
            collapseWhitespace: true,
            removeAttributeQuotes: true,
            removeComments: true,
            filename: 'templates.js'
        }))
        .pipe(templateCache())
        .pipe(wrapper({
            header: '(function(angular){',
            footer: '})(angular);'
        }));

    var code = gulp.src(paths.js);

    return merge(code, templates)
        .pipe(sourcemaps.init())
        //.pipe(print(function (filepath) {
        //    return "built: " + filepath;
        //}))
        .pipe(jshint())
        .pipe(jshint.reporter('default'))
        .pipe(concat('jasify.js'))
        .pipe(gulp.dest(paths.build + '/js'))
        .pipe(ngAnnotate())
        .pipe(uglify())
        .pipe(rename({extname: '.min.js'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/js'));
}

function testClientJsHint(cb) {
    return gulp.src(paths.test.js)
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
function rebuild() {
    gulp.watch(paths.js.concat(paths.partials), ['javascript']);
    gulp.watch(paths.test.js, ['javascript-test-jshint']);
    gulp.watch(paths.css, ['stylesheet']);
    gulp.watch(paths.index, ['index']);
    gulp.watch(paths.examples, ['examples']);
}
