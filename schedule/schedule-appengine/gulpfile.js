'use strict';

var argv = require('yargs').argv;
var gulp = require('gulp');
var merge = require('merge-stream');
var print = require('gulp-print');
var gutil = require('gulp-util');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var jshint = require('gulp-jshint');
var minifyCSS = require('gulp-minify-css');
var htmlmin = require('gulp-htmlmin');
var rename = require('gulp-rename');
var concat = require('gulp-concat');
var wrapper = require('gulp-wrapper');
var del = require('del');
var symlink = require('gulp-sym');
var bower = require('gulp-bower');
var karma = require('gulp-karma');
var gulpif = require('gulp-if');
var templateCache = require('gulp-angular-templatecache');
var paths = require('./paths.json');

gulp.task('clean', function (cb) {
    del([paths.build, paths.symBuild], cb);
});

gulp.task('sym', ['build'], function (cb) {
    return gulp
        .src(paths.build)
        .pipe(symlink(paths.symBuild, {force: true, relative: true}));
});

gulp.task('bower', function (cb) {
    return bower()
        .pipe(gulp.dest(paths.build + '/lib'));
});

gulp.task('javascript', function (cb) {
    var templates = gulp.src(paths.partials)
        .pipe(htmlmin({
            collapseWhitespace: true,
            removeAttributeQuotes: true,
            removeComments: true,
            filename: 'templates.js',
            templateHeader: '',
            templateFooter: ''
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
});

gulp.task('javascript-test-jshint', function (cb) {
    return gulp.src(paths.test.js)
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

gulp.task('stylesheet', function (cb) {
    return gulp.src(paths.css)
        .pipe(sourcemaps.init())
        //.pipe(less())
        .pipe(concat('jasify.css'))
        .pipe(gulp.dest(paths.build + '/css'))
        .pipe(minifyCSS())
        .pipe(rename({extname: '.min.css'}))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(paths.build + '/css'));
});

gulp.task('index', function (cb) {
    return gulp.src(paths.index)
        .pipe(htmlmin({collapseWhitespace: true, minifyJS: true}))
        .pipe(gulp.dest(paths.build + '/../'))
});

gulp.task('examples', function (cb) {
    return gulp.src(paths.examples)
        .pipe(gulp.dest(paths.build + '/../'))
});

gulp.task('html', ['index', 'examples']);

gulp.task('test', ['build'], function (cb) {
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
});

gulp.task('watch', function () {
    gulp.watch(paths.js.concat(paths.partials), ['javascript']);
    gulp.watch(paths.test.js, ['javascript-test-jshint']);
    gulp.watch(paths.css, ['stylesheet']);
    gulp.watch(paths.index, ['index']);
    gulp.watch(paths.examples, ['examples']);
});

gulp.task('build', ['javascript', 'stylesheet', 'html', 'bower']);

gulp.task('default', ['watch', 'sym', 'build', 'javascript-test-jshint']);