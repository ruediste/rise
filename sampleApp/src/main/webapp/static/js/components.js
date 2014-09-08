var components = (function () {
 
	
  var myPrivateVar, myPrivateMethod;
 
  // A private counter variable
  myPrivateVar = 0;
 
  // A private function which logs any arguments
  myPrivateMethod = function( foo ) {
      console.log( foo );
  };
  
  generateKey=function(componentId, key){
	  return "c_"+componentId+"_"+key;
  };
 
  return {
     register: function( bar ) {
 
    	// handler for partial reloads
    		$(document).on("c_viewReload",".c_reload",function(event){
    			var receiver=$(this);
    			var data=receiver.serialize();
    			event.preventDefault();
    			event.stopPropagation();
    			var foo=$("body").attr("data-reloadpath");
    			$.ajax({
    				  type: "POST",
    				  url: $("body").attr("data-reloadpath")+"/"+receiver.children("._componentId").first().text(),
    				  data: data,
    				  success: function(data){
    						// receiver.replaceWith($(data).children());
    						receiver.replaceWith(data);
    					},
    				  dataType: "html"
    				});
    		});
    		
    		$(document).on("submit", function(event){
    			$(this).trigger("c_viewReload");
    			return false;
    		});
    		
    		$(document).on("click",".c_button", function(){
    			var componentId=$(this).children("._componentId").first().text();
    			$(this).after("<input type=\"text\" class=\"c_hidden\" name=\""+generateKey(componentId,"clicked")+"\" />");
    			$(this).trigger("c_viewReload");
    			return false;
    		});
    }
  };
 
})();

$(components.register);
