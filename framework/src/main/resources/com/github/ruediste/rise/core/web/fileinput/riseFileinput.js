(function() {
	var reload=function(receiver){
		receiver.find('input.rise_fileinput[type=file]').fileinput();
	}
	// perform on reloads
	rise.onReload.add(reload);
	
	// initialize all file inputs
	$(function(){reload($(document));});
})();