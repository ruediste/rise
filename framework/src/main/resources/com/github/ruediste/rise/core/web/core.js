var rise = (function() {
	var getComponentNr = function(element){
		return  element.data("rise-component-nr");
	}
	var generateKey = function(element, key) {
		return "c_" +getComponentNr(element) + "_" + key;
	};

	var getPageNr = function(){
		return $("body").data("rise-page-nr");
	}
	/**
	 * Enters an endless loop polling for an application restart
	 */
	var pollForApplicationRestart = function() {
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
	/**
	 * Enters an endless loop sending the heart beat to the server
	 */
	var sendHearbeats = function() {
		var url = $("body").data("rise-heartbeat-url");
		if (!url) {
			// end the heartbeat loop
		} else {
			$.ajax({
				url : url,
				method : "POST",
				data : {
					"nr" : getPageNr()
				},
				complete : function() {
					window.setTimeout(sendHearbeats, $("body").data("rise-heartbeat-interval"));
				}
			});
		}
	};

	var onReload = $.Callbacks();

	var extractData = function(data, element) {
		var send = element.data("riseSend");
		if (send !== undefined) {
			send.split(" ").forEach(function(e) {
				var value = element.data(e);
				if (value)
					data.push({
						name : generateKey(element, e),
						value : value
					});
			});
		}

		element.children().each(function(idx, e) {
			extractData(data, $(e));
		});
	}

	var reload = function(receiver) {
		receiver = $(receiver);

		// extract data
		var data = receiver.serializeArray();
		extractData(data, receiver);

		// perform request
		$.ajax({
			method : "POST",
			url : $("body").data("rise-reload-url") + "?page="
					+ getPageNr() + "&nr="
					+ getComponentNr(receiver),
			data : JSON.stringify(data),
			contentType : "text/json; charset=UTF-8",
			processData : false,
			success : function(data, status, jqXHR) {
				var redirectTarget = jqXHR
						.getResponseHeader("rise-redirect-target");
				if (redirectTarget) {
					window.location = redirectTarget;
				} else {
					receiver.get(0).innerHTML = data;
					$("body").attr("data-rise-reload-count",
							1 + $("body").attr("data-rise-reload-count"));
					onReload.fire(receiver);
				}
			},
			dataType : "html",
			headers : {
				"rise-is-ajax" : "true"
			}
		});
	};

	// Register for document load event
	$(function() {
		// handler for reloads
		$(document).on("rise_viewReload", ".rise_reload", function(event) {
			event.preventDefault();
			event.stopPropagation();
			reload(this);
		});

		// clicks on rise_buttons trigger a view reload
		$(document).on("click", ".rise_button", function() {
			$(this).data("riseSend", "riseIntClicked");
			$(this).data("riseIntClicked", "clicked");
			$(this).trigger("rise_viewReload");
			return false;
		});

		// start polling for application restart
		pollForApplicationRestart();

		// trigger an initial reload
		onReload.fire($(document));
	});

	
	
	return {
		onReload : onReload,
		generateKey : generateKey
	};

})();

// register autocomplete
rise.onReload.add(function() {
	$(".rise_autocomplete").each(function(idx, element) {
		element = $(element);
		element.autocomplete({
			source : element.data("riseIntSource"),
			change : function(event, ui) {
				if (ui.item)
					element.data("riseIntChosenItem", ui.item.id);
				else
					element.data("riseIntChosenItem", null);
			}
		});
	});
});