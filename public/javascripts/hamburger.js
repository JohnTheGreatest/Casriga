jQuery(document).ready(function() {

    //Open the menu
    jQuery("#hamburger").click(function() {

        //set the width of primary content container -> content should not scale while animating
        var contentWidth = jQuery('.main').width();

        //set the content with the width that it has originally
        jQuery('.main').css('width', contentWidth);

        //display a layer to disable clicking and scrolling on the content while menu is shown
        jQuery('#content-layer').css('display', 'block');

        //disable all scrolling on mobile devices while menu is shown
        /*jQuery('.mainpage').bind('touchmove', function(e){e.preventDefault()});*/

        //set margin for the whole container with a jquery UI animation
        jQuery(".main").animate({"marginLeft": ["70%", 'easeOutExpo']}, {
            duration: 700
        });

    });

    //close the menu
    jQuery("#content-layer").click(function() {

        //enable all scrolling on mobile devices when menu is closed
        jQuery('.main').unbind('touchmove');

        //set margin for the whole container back to original state with a jquery UI animation
        jQuery(".main").animate({"marginLeft": ["0", 'easeOutExpo']}, {
            duration: 700,
            complete: function() {
                  jQuery('.main').css('width', 'auto');
                  jQuery('#content-layer').css('display', 'none');
            }
        });
    });

});