var gulp = require('gulp');
var plugins = require("gulp-load-plugins")({
    pattern: ['gulp-*', 'gulp.*', 'main-bower-files'],
    replaceString: /\bgulp[\-.]/
});

var dest='build/';
gulp.task('default',['js','css','assets']);

gulp.task('watch', function() {
     gulp.watch('src/**', ['default']); 
});


gulp.task('assets', function() {
    gulp.src(['bower_components/bootstrap/fonts/*'])
    .pipe(gulp.dest(dest + '/fonts'));
});

gulp.task('js', function() {
    gulp.src(plugins.mainBowerFiles().concat(['src/**/*.js']))
	.pipe(plugins.filter('*.js'))
	.pipe(plugins.concat({path: 'all.js'}))
	.pipe(plugins.uglify())
	.pipe(gulp.dest(dest));

});

gulp.task('css', function() {
	var filter = plugins.filter(['*.less'], {restore: true});
	gulp.src(plugins.mainBowerFiles() .concat(['src/**/*.less','src/**/*.css']))
	.pipe(plugins.sourcemaps.init())
	.pipe(filter)
	.pipe(plugins.less())
	.pipe(filter.restore)
        .pipe(plugins.filter('*.css'))
	.pipe(plugins.order([
	    'normalize.css',
	    '*'
	]))
	.pipe(plugins.minifyCss({ relativeTo: './src', target: './src' }))
	.pipe(plugins.concat({path: 'all.css'}))
	.pipe(plugins.rev())
	.pipe(plugins.sourcemaps.write())
	.pipe(gulp.dest(dest))
	.pipe(plugins.rev.manifest())
	.pipe(gulp.dest(dest))
;
});

