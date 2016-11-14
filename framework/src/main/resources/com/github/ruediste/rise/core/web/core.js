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
	
	var pushUrls = function (jsonString){
	  if (!jsonString)
	    return;
	  JSON.parse(jsonString).forEach(function(url){
	    if (url==null)
	      history.back();
	    else
	  	  history.pushState({},"",url);
	  });
	};
	
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
	var toggled = $.Callbacks();

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
			    pushUrls(jqXHR.getResponseHeader("rise-pushed-urls"));
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
		
		// swallow submit events
		$(document).on("submit", ".rise_reload", function(event) {
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
		
		// push initially pushed URLs
		pushUrls($("body").data("rise-pushed-urls"));

		// start polling for application restart
		pollForApplicationRestart();

		// trigger an initial reload
		onReload.fire($(document));
	});

	return {
		onReload : onReload,
		
		// setExtractData(element, function($element, data))
		// data: [{name:"...", value:"..."},...]
		setExtractData : setExtractData,
		triggerViewReload : triggerViewReload,
		// fired to indicate that an element has been toggled. The argument is the component id of the toggled component
		toggled : toggled,
		
	    debounce: function(fn, timeout){
		  var timeoutId = -1;
		  return function(){
			if (timeoutId>-1){
				window.clearTimeout(timeoutId);
			}
			timeoutId = window.setTimeout(fn, timeout);
		  };
	    }
	};

})();

// cdisplay
rise.toggled.add(function(nr){
  $("*[data-rise-cdisplay-nr=\""+nr+"\"]").toggleClass("rise-cdisplay-none");
});
rise.onReload.add(function($receiver){
  $receiver.find("*[data-rise-cdisplay-displayed]").each(function(idx,element){
    var $element=$(element);
    if (!$element.data("riseCdisplayDisplayed"))
      $element.addClass("rise-cdisplay-none");
    rise.setExtractData(element, function(data){
      data.push({
        name:$element.data("riseCdisplayKey"),
        value:!$element.hasClass("rise-cdisplay-none")
      });
    });
  });
});

// ConClickToggle
$(function(){
  $(document).on("click", "*[data-rise-conclicktoggle-target]", 
			function(evt) {
			    rise.toggled.fire($(this).data("rise-conclicktoggle-target"));
				return false;
			});
});

// COptionalInputBase
$(function(){
  $(document).on("click", ".rise-cOptionalInputBase._checked > ._check > input", function(evt) {
  				$(this).parent().parent().toggle();
			    $(this).parent().parent().next().toggle();
			    $(this).parent().parent().prev().click();
				return false;
			});
  $(document).on("click", ".rise-cOptionalInputBase._unchecked > ._check > input", function(evt) {
                $(this).parent().parent().toggle();	
			    $(this).parent().parent().prev().toggle();
			    $(this).parent().parent().prev().prev().click();
				return false;
			});
});
