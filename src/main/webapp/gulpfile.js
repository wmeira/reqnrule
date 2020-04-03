'use strict';

var gulp = require('gulp'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    minifyCss = require('gulp-minify-css'),
    del = require('del');

gulp.task('clean', function (cb) {
    return del([
        './_build/'
    ], cb);
});


gulp.task('js', ['clean'], function () {
    return gulp.src(['app/js/app.js', 'app/js/**/*.js'])
        .pipe(concat('app.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('./_build/js'))
})

gulp.task('css', ['clean'], function () {
    return gulp.src(['app/styles/*.css'])
        .pipe(concat('style.min.css'))
        .pipe(minifyCss({keepBreaks:true}))
        .pipe(gulp.dest('./_build/css'))
})

gulp.task('default', ['js', 'css']);
