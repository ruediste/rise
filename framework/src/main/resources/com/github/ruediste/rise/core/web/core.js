var rise = (function() {
	var generateKey = function(componentId, key) {
		return "c_" + componentId + "_" + key;
	};

	/**
	 * Enters an endless loop polling for an application restart
	 */
	pollForApplicationRestart = function() {
		var url = $("body").data("rise-restart-query-url");
		if (!url) {
			// end the reload check loop
		} else {
			$.ajax({
				url : url,
				method : "POST",
				data : {
					"nr" : $("body").data("rise-restart-nr")
				},
				success : function(data) {
					if (data == "true")
						window.location.reload();
					else
						pollForApplicationRestart();
				},
				error : function() {
					window.setTimeout(pollForApplicationRestart, 2000);
				}

			});
		}
	};
	
	var onReload=$.Callbacks();
	
	var reload=function(receiver){
	    receiver = $(receiver);
		var data = receiver.serialize();
		$.ajax({
			method : "POST",
			url : $("body").data("rise-reload-url") + "?page="+$("body").data("rise-page-nr")+"&nr="
					+ receiver.data("rise-component-nr"),
			data : data,
			success : function(data) {
				var dom=$.parseHTML(data);
				receiver.replaceWith(dom);
				onReload.fire($(dom));
			},
			dataType : "html"
		});
	};
	
	// Register for document load event
	$(function() {
		// handler for  reloads
		$(document).on(
				"rise_viewReload",
				".rise_reload",
				function(event) {
					event.preventDefault();
					event.stopPropagation();
					reload(this);
				});


		// clicks on rise_buttons trigger a view reload
		$(document).on(
				"click",
				".rise_button",
				function() {
					// add the button id as hidden input
					var componentId = $(this).data("rise-component-nr");
					$(this).after(
							"<input type=\"text\" class=\"rise_hidden\" name=\""
									+ generateKey(componentId, "clicked")
									+ "\" />");
					$(this).trigger("rise_viewReload");
					return false;
				});

		// start polling for application restart
		pollForApplicationRestart();
	});

	return {
		onReload: onReload
	};

})();
