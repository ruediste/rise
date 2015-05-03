function initReload (url, nr) {
	function doQuery(){
		$.ajax({
			  url: url,
			  method: "POST",
			  data: {"nr":nr}
			}).done(function(data) {
				if (data == "true")
					window.location.reload();
				else
					doQuery();
			});
	}
	$(function(){
		doQuery();
	});
}