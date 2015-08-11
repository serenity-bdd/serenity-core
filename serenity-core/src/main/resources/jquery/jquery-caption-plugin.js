/*
 * Injects a listener for element interaction so that elements are highlighted in the UI.
 */
(function($) {
    var focusedElement = null;
    $("*").live("click", function(e){
        var el = $(this);
        if(focusedElement == null){
            $.thucydidesHighlighter.unhighlight();
            $.thucydidesHighlighter.highlight(el);
            focusedElement = el;
        }
    });
    $.thucydidesHighlighter = {
        unhighlight: function(){
            var el = $('body').data('highlight');
            if(el != null){
                var originalStyle = $('body').data('thucydidesHighlighter-originalStyle');
                //highlighted.css('border', highlightedBorder);
                $('body').data('thucydidesHighlighter-highlight', null);

                el.css({
                    '-moz-box-shadow': originalStyle,
                    '-webkit-box-shadow': originalStyle,
                    'box-shadow': originalStyle
                });
            }
        },
        highlight: function(el){
            var originalStyle = el.css('box-shadow');
            //el.css('border', '1px solid #F00');
            $('body').data('thucydidesHighlighter-originalStyle', originalStyle);
            $('body').data('thucydidesHighlighter-highlight', el);
            var style = 'inset 0 0 3px green';
            el.css({
                '-moz-box-shadow': style,
                '-webkit-box-shadow': style,
                'box-shadow': style
            });
        },
        reset: function(){
            focusedElement = null;
        }
    };
})(jQuery);