var gulp = require('gulp');

var plugins = require("gulp-load-plugins")({
	pattern : [ 'gulp-*', 'gulp.*', 'main-bower-files' ],
	replaceString : /\bgulp[\-.]/
});

gulp.task('default', function() {
		return gulp.src(['jsTests/*.js'], { read: false })
				.pipe(plugins.mocha({ reporter: 'list',  globals: {
				}}))
				.on('error', plugins.util.log);
});
