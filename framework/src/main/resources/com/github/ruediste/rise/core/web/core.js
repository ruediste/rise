var rise = (function() {

	var getPageNr = function() {
		return $("body").data("rise-page-nr");
	};

	var setExtractData = function(element, func) {
		$(element).data("riseExtractData", func);
	};

	var triggerViewReload = function(element) {
		$(element).trigger("rise_viewReload");
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
					window.setTimeout(sendHearbeats, $("body").data(
							"rise-heartbeat-interval"));
				}
			});
		}
	};

	var onReload = $.Callbacks();

	var extractData = function(data, $element) {

		var extractFunc = $element.data("riseExtractData");
		if (extractFunc !== undefined) {
			extractFunc.call($element, data);
		}
		
		/*var key=$element.data("riseSendValue");
		if (key){
		  data.push({name:key, value:$element.attr("value")});
		}*/

		$element.children().each(function(idx, e) {
			extractData(data, $(e));
		});
	}

	var reload = function(receiver) {
		var $receiver = $(receiver);

		// extract data
		var data = $receiver.serializeArray();
		extractData(data, $receiver);

		// perform request
		$.ajax({
			method : "POST",
			url : $("body").data("rise-reload-url") + "?page=" + getPageNr()
					+ "&fragmentNr=" + $receiver.data("riseFragmentnr"),
			data : JSON.stringify(data),
			contentType : "text/json; charset=UTF-8",
			processData : false,
			success : function(data, status, jqXHR) {
				var redirectTarget = jqXHR
						.getResponseHeader("rise-redirect-target");
				if (redirectTarget) {
					window.location = redirectTarget;
				} else {
					receiver.innerHTML = data;
					onReload.fire($receiver);
				}
			},
			error : function(jqXHR, testStatus, errorThrown){
				$("html").get(0).innerHTML=jqXHR.responseText;
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
		
		$(document).on("submit", ".rise_reload", function(event) {
			// cancel submit events
			event.preventDefault();
			event.stopPropagation();
		});

		// register generic event handlers
		[ "focusin", "focusout", "click" ].forEach(function(event) {
			$(document).on(event, "*[data-rise-on-" + event + "]",
					event, 
			function(evt) {
				setExtractData(this, function(data) {
					var $this=$(this);
					var kvp=$this.data("rise-on-"+event);
					var idx=kvp.indexOf("=");
					if (idx==-1){
						data.push({
							name : kvp,
							value : "triggered"
						});
					}
					else {
						data.push({
							name : kvp.substring(0,idx),
							value : kvp.substring(idx+1)
						});
					}
				});
				triggerViewReload(this);
				return false;
			});
		});
		
		

		// start polling for application restart
		pollForApplicationRestart();

		// trigger an initial reload
		onReload.fire($(document));
	});

	return {
		onReload : onReload,
		setExtractData : setExtractData,
		triggerViewReload : triggerViewReload
	};

})();
