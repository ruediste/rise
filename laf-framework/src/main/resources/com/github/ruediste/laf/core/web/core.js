var rise = (function() {

	var myPrivateVar, myPrivateMethod;

	// A private counter variable
	myPrivateVar = 0;

	// A private function which logs any arguments
	myPrivateMethod = function(foo) {
		console.log(foo);
	};

	generateKey = function(componentId, key) {
		return "c_" + componentId + "_" + key;
	};
	
	pollForApplicationRestart = function(){
		$.ajax({
			  url: $("body").data("rise-restart-query-url"),
			  method: "POST",
			  data: {"nr": $("body").data("rise-restart-nr") }
			}).done(function(data) {
				if (data == "true")
					window.location.reload();
				else if (data == "false")
					pollForApplicationRestart();
				else {
					window.setTimeout(pollForApplicationRestart,2000);
				}
			});
	}

	return {
		register : function() {

			// handler for partial reloads
			$(document).on(
					"rise_viewReload",
					".rise_reload",
					function(event) {
						var receiver = $(this);
						var data = receiver.serialize();
						// for (var attrname in obj2) { obj1[attrname] = obj2[attrname]; }
						event.preventDefault();
						event.stopPropagation();
						$.ajax({
							type : "POST",
							url : $("body").data("rise-reloadurl") + "/"
									+ receiver.data("rise-component-nr"),
							data : data,
							success : function(data) {
								// receiver.replaceWith($(data).children());
								receiver.replaceWith(data);
							},
							dataType : "html"
						});
					});

			// submit buttons trigger a view reload
			$(document).on("submit", function(event) {
				$(this).trigger("rise_viewReload");
				return false;
			});

			// clicks on rise_buttons trigger a view reload
			$(document).on(
					"click",
					".rise_button",
					function() {
						// add the button id as hidden input (TODO: can this be sent along with the event?)
						var componentId = $(this).data("c-component-nr");
						$(this).after(
								"<input type=\"text\" class=\"rise_hidden\" name=\""
										+ generateKey(componentId, "clicked")
										+ "\" />");
						$(this).trigger("rise_viewReload");
						return false;
					});
			
			// start polling for application restart
			pollForApplicationRestart();
		}
	};

})();

$(rise.register);
