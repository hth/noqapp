 

(function($) {

	$.fn.menupuncher = function(options) {
		
		$(this).addClass("menupuncher");
		
		// Settings
		var settings = $.extend({
			color  : ' ',
			textcolor  : ' ',
			opacity  : '1',
			hovercolor: ''
		}, options);
			
		if ( settings.color ) {
			var colorx = settings.color;			
		}
		if ( settings.hovercolor ) {
			var hovercolorx = settings.hovercolor;			
		}
		if ( settings.opacity ) {
			var opacityx = settings.opacity;
		}
		if ( settings.textcolor ) {
			var textcolorx = settings.textcolor;
		}
		$('.menupuncher').hide();
												
		$('.pusher-menu').click(function() {	
		
				$('.pusher-menu').toggleClass('click');
															
									
				if($('.pusher-menu').hasClass('click')){
					$(".menupuncher").wrap("<nav class='open'><div class='bg-cover'></div></nav>");
					
					$("body").bind("touchmove",function(e){e.preventDefault();});
					
					$('.menupuncher').show();
					
					$('.open').css("background-color",colorx).css('opacity', opacityx);
					$('.open li a').css("color",textcolorx);
					
					$(".open a").mouseover(function() {
						$(this).css("background-color",hovercolorx);
						$(this).css("color",colorx);
					}).mouseout(function() {
						$(this).css("background-color","transparent");
						$(this).css("color",textcolorx);
					});					
					
				}else{
					$(".menupuncher").unwrap().unwrap();
					
					$("body").unbind("touchmove",function(e){e.preventDefault();});
					
					$('.menupuncher').hide();
					
				}
												
				return false;
			});
		
		$(window).scroll( function() {
			if ($(window).scrollTop() > $('body').offset().top)
				$('.pusher-menu').addClass('floating');
			else
				$('.pusher-menu').removeClass('floating');
		} );
					
	}
	
	return false;
		
}(jQuery));	
