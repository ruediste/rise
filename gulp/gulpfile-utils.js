/**
Build file reading bundles from the assets/ subdirectory.

Each bundle is described by a bundle.js:
  module.exports={segments: [...]}

A bundle consists of a segment list. If the export is an array instead of an object,
the array is treated as segments list.

Each segment can be a single string, which results in an import. If the segement is an
object, the object is treated as segment descriptor. An array is treated as a segment
descriptor with it's srcs set to the array.

The srcs of a segment are normalized as follows:
A string results in a single source. An array results in a source with the array elements.
An object is treated as source.

Patterns are treated relative to the bower_components directory, unless they start with
a dot (for example "./*" )

Example:
module.exports= [
    [	// a segment
			"jquery/dist/jquery.min.js", // first src
	    "bootstrap/dist/js/bootstrap.min.js", // second src
	    ["rise-bower/rise-core.*","!rise-core.min.js"] // third src with two patterns
		],
		["./*"] // second segment, with a single source
  ];
*/

var path= require('path');
var gulp;
var dir = require('node-dir');
var fs = require('fs');
var merge2 = require('merge2');
var merge = streams=>merge2.apply(merge2,streams);

var plugins = require("gulp-load-plugins")({
	pattern : [ 'gulp-*', 'gulp.*', 'main-bower-files' ],
	replaceString : /\bgulp[\-.]/
});

function extend(target) {
    var sources = [].slice.call(arguments, 1);
    sources.forEach(function (source) {
        for (var prop in source) {
            var src=source[prop];
            if (src==undefined)
              delete target[prop];
            else
              target[prop] = source[prop];
        }
    });
    return target;
}

function readBundle(root){
  // console.log('Reading bundle ',root);
	var parts=root.split(path.sep);
	var bundleName=parts[parts.length-1];
	var bundleJsPath=root+path.sep+'bundle.js';

	var bundle;
	if (fs.existsSync(bundleJsPath)){
		bundle=require(bundleJsPath);
	}
	else bundle=[['*']];
  if (Array.isArray(bundle)) bundle={segments: bundle};
	bundle.bundleRoot=root;
	bundle.bundleName=bundleName;
	return bundle;
}

function normalizeBundle(input, ctx){
		return extend({}, ctx, input, {segments: normalizeSegments(input.segments, extend({},ctx,input,{segments: undefined}) )});
}

/* Input is an array of segments. sample s
  'common', // import
  ['react/*', '!*.css'], // two srcs
	['*.css', ['react/*', '!*.css']] // 2 srcs
	['*.js', { src: [], base: '...' }] // 2 srcs
  {srcs: [...], base: '...'}
*/
function normalizeSegments(inputArray, ctx){
	var result=[];
	inputArray.forEach(input=>{
		if (typeof(input)=='string'){
			// includes// process include
			var includeRoot=assetsBase+path.sep+input;
			var includeBundle=readBundle(includeRoot);
			normalizeBundle(includeBundle, ctx).segments.forEach(e=>result.push(e));
		}
		else if (Array.isArray(input)){
			result.push(extend({},ctx,{srcs: normalizeSrcs(input,ctx)}));
		}
		else{
			result.push(extend({}, ctx, input, {srcs: normalizeSrcs(input.srcs, extend({},ctx,input,{srcs:undefined}))}));
		}
	});
	return result;
}

/* Sample Inputs:
'*.css' => [{src: ['*.css']}]
['*.css', 'foo.css'] => [{src: ['*.css']}, {src: ['foo.js'] }]
['*.css', ['*.js', '!foo.js']] =>[{src: ['*.css']}, {src: ['*.js', '!foo.js'] }]
[{src: ['*.css']}] => [{src: ['*.css']}]
*/
function normalizeSrcs(input, ctx){
  if (typeof(input)=='string'){
		return [extend({},ctx,{src:[input]})];
	} else if (Array.isArray(input)){
		return input.map(e=>{
			if (typeof(e)=='string'){
				return extend({},ctx,{src: [e]});
			} else if (Array.isArray(e)){
				return extend({},ctx,{src: e});
			} else return extend({},ctx,e);
		});
	} else {
	  throw 'input is neither string nor array';
	}
}



var debug=function(opts){
	return plugins.ifElse(false, ()=>plugins.debug(opts));
}

var dest = 'target/classes/assets/';
var bundleDest = 'src/main/resources/assets/';
var assetsBase;

function splitBy(input, separator){
	var value=[];
	var current=[];
	var result=[];
	for (var i=0; i<input.length; i++){
		var val=separator(input[i]);
		if (value.length>0 && value[0]!==val){
			result.push({key: value[0], value: current});
			current=[];
		}
		if (value.length==0 || value[0]!==val)
		  value=[val];

		current.push(input[i]);
	}
	if (current.length>0)
	  result.push({key: value[0], value: current});
	return result;
}

function readSegment(segment){
	return merge(segment.srcs.map(src=>{
		var defaults={};
		if (src.src.some(s=>s.startsWith('./'))){
			extend(defaults, {base: src.bundleRoot, cwd: segment.bundleRoot});
		}
		else{
			extend(defaults, {base: 'bower_components/', cwd: 'bower_components/'});
		}
		return gulp.src(src.src, extend(defaults, src,{src: undefined}))
		.pipe(plugins.plumber({errorHandler: function (err) {
            console.log(err);
            this.emit('end');
        }}));
	}))
	.pipe(plugins.sourcemaps.init());
}

var allSegmentTasks=[];
var allSegmentWatchTasks=[];
var allBundleTasks=[];

function defineBundle(root) {
	var bundle=normalizeBundle(readBundle(root),{});
	// init watch of segments
	bundle.segments.forEach(segment=>{
		if (segment.watch==undefined){
			segment.watch=segment.srcs.some(src=>src.src.some(s=>s.startsWith('./')||s.startsWith('!./')));
		}
	});

	var bundleName=bundle.bundleName;
	//console.log(bundleName+':', bundle);

	var watchTasks=[];
	var tasks=[];

  bundle.segments.forEach(function(segment, segmentNr){
		var segment=bundle.segments[segmentNr];
		var taskName='segment-'+bundle.bundleName+'-'+segmentNr;
		tasks.push(taskName);
		if (segment.watch){
			watchTasks.push(taskName);
		}
		gulp.task(taskName ,function(done) {
			const jsFilter = plugins.filter(['**/*.js','!**/bundle.js'], {restore: true});
			const sassFilter = plugins.filter('**/*.scss', {restore: true});
			const cssFilter = plugins.filter(['**/*.css','**/*.scss'], {restore: true});

			return readSegment(segment)
			.pipe(jsFilter)
			.pipe(debug({title : bundle.bundleName+'jsIn'+segmentNr}))
			.pipe(plugins.ifElse(segment.watch,()=>plugins.babel({
					only: [
						'src/**',
					],
					presets: [require('babel-preset-react')],
					compact: false
				})))
		  .pipe(plugins.concat(bundle.bundleName+'-segment-'+segmentNr+'.js'))
			.pipe(plugins.sourcemaps.write('./maps'))
			.pipe(debug({title : bundle.bundleName+'jsOut'+segmentNr}))
			.pipe(gulp.dest(dest))
			.pipe(jsFilter.restore)

			.pipe(cssFilter)
			.pipe(sassFilter)
			.pipe(plugins.sass().on('error', plugins.sass.logError))
			.pipe(sassFilter.restore)
			.pipe(debug({title : bundle.bundleName+'cssIn'+segmentNr}))
			.pipe(plugins.concatCss(bundle.bundleName+'-segment-'+segmentNr+'.css'))
			.pipe(plugins.sourcemaps.write('./maps'))
			.pipe(debug({title : bundle.bundleName+'cssOut'+segmentNr}))
			.pipe(gulp.dest(dest))
			.pipe(cssFilter.restore);
		});
	});

  var segmentsTaskName='segments-'+bundleName;
  allSegmentTasks.push(segmentsTaskName);
  gulp.task(segmentsTaskName, tasks);

  allSegmentWatchTasks.push('segments-'+bundleName+'-watch');
  gulp.task('segments-'+bundleName+'-watch', function(){
    return gulp.watch([root+'/**/*.*'], watchTasks);
	});

  allBundleTasks.push('bundle-'+bundle.bundleName+'-js');
	gulp.task('bundle-'+bundle.bundleName+'-js',[segmentsTaskName], function() {
		return gulp.src(
			bundle.segments.map((e,idx)=>'./'+dest+bundle.bundleName+'-segment-'+idx+'.js'))
		.pipe(debug({	title : bundle.bundleName+'js In'	}))
		.pipe(plugins.concat(bundle.bundleName+'.js'))
		// .pipe(plugins.uglify())
		.pipe(plugins.sourcemaps.write('./maps'))
		.pipe(plugins.hash())
		.pipe(debug({title : bundle.bundleName+'js Out'}))
		.pipe(gulp.dest(bundleDest))
		.pipe(plugins.filelist(bundle.bundleName+'.js.list',{flatten: true}))
		.pipe(gulp.dest(bundleDest));
	});

  allBundleTasks.push('bundle-'+bundle.bundleName+'-css');
	gulp.task('bundle-'+bundle.bundleName+'-css',[segmentsTaskName], function() {
		return gulp.src(
			bundle.segments.map((e,idx)=>'./'+dest+bundle.bundleName+'-segment-'+idx+'.css'))
		.pipe(debug({title : bundle.bundleName+'css In'}))
		.pipe(plugins.concat(bundle.bundleName+'.css'))
		// .pipe(plugins.cleanCss())
		.pipe(plugins.hash())
		.pipe(debug({title : bundle.bundleName+'css Out'}))
		.pipe(gulp.dest(bundleDest))
		.pipe(plugins.filelist(bundle.bundleName+'.css.list',{flatten: true}))
		.pipe(gulp.dest(bundleDest));
	});
}

function defineTasks(_gulp, base){
	assetsBase=base+"/src/main/assets/";
	gulp=_gulp;
	fs.readdirSync(assetsBase).forEach(function(path){
	  defineBundle(assetsBase+path);
	});

	gulp.task('others', function() {
		return gulp.src([ 'bower_components/**/*.*' ,assetsBase+"/**/*.*"])
		// .pipe(debug({title:'others in'}))
		.pipe(
			plugins.filter([ '**', '!**/*.js', '!**/*.less', '!**/*.css','!**/*.txt','!**/*.map','!**/*.json','!**/*.yml',
				'!**/*.ps1','!bower_components/bootstrap/fonts/**',
						'!**/*.md', '!**/*.nuspec', '!**/*.jsx', '!**/*.sublime-project' ]))
		.pipe(debug({	title : 'others out'	}))
		.pipe(gulp.dest(dest));
	});

	gulp.task('clean', function () {
	  return require('del')([dest+'**/*', bundleDest+'**/*']);
	});

	gulp.task('watch', allSegmentWatchTasks);
	gulp.task('all', allBundleTasks.concat(['others']));
	gulp.task('default', [ 'others', 'watch' ].concat(allSegmentTasks));


};

module.exports={
	defineTasks: defineTasks,
  private: {
		extend: extend,
		normalizeBundle: normalizeBundle,
		readBundle : readBundle,
    normalizeSrcs: normalizeSrcs,
    normalizeSegments: normalizeSegments
  }
};
