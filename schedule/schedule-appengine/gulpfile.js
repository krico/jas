'use strict';

var gulp = require('gulp');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var jshint = require('gulp-jshint');
var minifyCSS = require('gulp-minify-css');
var minifyHTML = require('gulp-minify-html');
var rename = require('gulp-rename');
var concat = require('gulp-concat');
var del = require('del');
var symlink = require('gulp-sym');
var bower = require('gulp-bower');
var karma = require('gulp-karma');
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
    return gulp.src(paths.js)
        .pipe(sourcemaps.init())
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

gulp.task('html', function (cb) {
    //var opts = {comments: false, spare: true, conditionals: true};
    var opts = {spare: true, conditionals: true};

    return gulp.src(paths.html)
        .pipe(minifyHTML(opts))
        .pipe(gulp.dest(paths.build + '/../'))
});

gulp.task('test', ['build'], function (cb) {
    return gulp.src([])
        .pipe(karma({configFile: paths.test.karmaConfig, cmd: 'start'}))
        .on('error', function (err) {
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
});

gulp.task('watch', function () {
    gulp.watch(paths.js, ['javascript']);
    gulp.watch(paths.css, ['stylesheet']);
    gulp.watch(paths.html, ['html']);
});

gulp.task('build', ['javascript', 'stylesheet', 'html', 'bower']);

gulp.task('default', ['watch', 'sym', 'build']);