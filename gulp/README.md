# RISE - gulp
Shared gulp-build used for RISE.

## Usage
Execute

		npm install --save-dev gulp rise-gulp

Create a gulpfile.js in the root of your project containing

    require('rise-gulp').defineTasks(require('gulp'), __dirname);

## Overview
The build reads assets from the src/main/assets subdirectory.

Each bundle is described by a bundle.js:
  module.exports={segments: [...]}

A bundle consists of a segment list. If the export is an array instead of an object,
the array is treated as segment list.

Each segment can be a single string, which results in an import. If the segment is an
object, the object is treated as segment descriptor. An array is treated as a segment
descriptor with it's srcs set to the array.

The srcs of a segment are treated as follows:
 * A string results in a single source.
 * An array results in a source with the array elements.
 * An object is treated as source.

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
