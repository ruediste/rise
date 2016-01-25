var rise = (function() {
	var getComponentNr = function(element) {
		return element.data("rise-component-nr");
	}
	var generateKey = function(element, key) {
		return "c_" + getComponentNr(element) + "_" + key;
	};

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

	var extractData = function(data, element) {

		var extractFunc = element.data("riseExtractData");
		if (extractFunc !== undefined) {
			extractFunc.call(element, data);
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
			url : $("body").data("rise-reload-url") + "?page=" + getPageNr()
					+ "&nr=" + getComponentNr(receiver),
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

		// clicks on rise_buttons trigger a view reload
		$(document).on("click", ".rise_button", function() {
			setExtractData(this, function(data) {
				data.push({
					name : rise.generateKey(this, "clicked"),
					value : "clicked"
				})
			});

			triggerViewReload(this);
			return false;
		});
		
		$(document).on("change", ".rise_c_select._selectionHandler", function() {
			triggerViewReload(this);
			return false;
		});

		// support for generic event handlers
		var eventHandler = function(evt) {
			setExtractData(this, function(data) {
				data.push({
					name : "event_triggered",
					value : $(this).attr("data-rise-reload-on-" + evt.data)
				})
			});
			triggerViewReload(this);
			return false;
		};

		// register generic event handlers
		[ "focusin", "focusout", "click" ].forEach(function(event) {
			$(document).on(event, "*[data-rise-reload-on-" + event + "]",
					event, eventHandler);
		});
		
		

		// start polling for application restart
		pollForApplicationRestart();

		// trigger an initial reload
		onReload.fire($(document));
	});

	return {
		onReload : onReload,
		generateKey : generateKey,
		setExtractData : setExtractData,
		triggerViewReload : triggerViewReload
	};

})();

$.widget("rise.riseAutocomplete", $.ui.autocomplete, {
	_renderItem : function(ul, item) {
		var result = $("<li>");
		if (item.testName)
			result.attr("data-test-name", item.testName);
		result.append($("<a>").text(item.label));
		result.appendTo(ul);
		return result;
	},
	_renderMenu : function(ul, items) {
		var that = this;
		if (this.element.attr("data-test-name"))
			$(ul).attr("data-test-name",
					"rise_autocomplete_" + this.element.attr("data-test-name"))
		$.each(items, function(index, item) {
			that._renderItemData(ul, item);
		});
	}
});

// register components for reload
rise.onReload.add(function(reloaded) {
	reloaded.find(".rise_button").each(function(idx, element) {
		rise.setExtractData(element, function(data) {
			if (this.data("rise-button-clicked"))
				data.push({
					name : rise.generateKey(this, "clicked"),
					value : "clicked"
				})
		});
	});

	reloaded.find(".rise_autocomplete").each(function(idx, element) {
		element = $(element);
		rise.setExtractData(element, function(data) {
			if (element.data("riseIntChosenItem"))
				data.push({
					name : rise.generateKey(element, "riseIntChosenItem"),
					value : element.data("riseIntChosenItem")
				})
		});
		element.riseAutocomplete({
			source : element.data("riseIntSource"),
			change : function(event, ui) {
				if (ui.item)
					element.data("riseIntChosenItem", ui.item.id);
				else
					element.data("riseIntChosenItem", null);
			}
		});
	});

	reloaded.find(".rise_sortable").each(function(idx, element) {
		element = $(element);
		element.children().each(function(childIdx, child) {
			$(child).attr("data-rise-sortable-index", childIdx);
		});
		element.sortable();
		rise.setExtractData(element, function(data) {
			data.push({
				name : rise.generateKey(this, "order"),
				value : this.sortable("toArray", {
					attribute : "data-rise-sortable-index"
				}).toString()
			})
		});
	});
});

// click edit
$(document).on("focusout", ".rise_click_edit._edit", function(event) {
	var callback = function(element) {
		rise.setExtractData(element, function(data) {
			data.push({
				name : rise.generateKey(element, "view")
			});
		});
		rise.triggerViewReload(element);
	}
	window.setTimeout(callback,0,$(this));
	return true;
});
$(document).on("click", ".rise_click_edit._view", function() {
	rise.setExtractData(this, function(data) {
		data.push({
			name : rise.generateKey(this, "edit")
		});
	});
	rise.triggerViewReload(this);
	return false;
});

rise.onReload.add(function(reloaded) {
	reloaded.find(".rise_click_edit._edit").each(function() {
		var focusId = $(this).data("rise-click-edit-focus-on-reload");
		if (focusId)
			$(document.getElementById(focusId)).focus();
	});
});