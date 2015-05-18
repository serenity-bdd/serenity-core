/**
 * JQuery tries to use native CSS selectors instead of the Sizzle selector
 * engine for performance reasons.
 *
 * This causes problems when trying to test intefaces using the
 * :focus pseudo selector as unless the web page and browser window
 * has the focus, all elements are considered to be without focus.
 * Checking for :focus in Selenium or Capybara tests therefore fail if
 * using JQuery or Sizzle.
 *
 * Sizzle will however return true for a :focus element even if the
 * window itself has lost focus if we force it not use the native selector functions
 * This script forces Sizzle to use its own engine over native selectors.
 *
 * This file MUST be included before JQuery or Sizzle is loaded
 *
 * Refer to https://github.com/mattheworiordan/jquery-focus-selenium-webkit-fix for more info
 *
 **/

/* replace the focus selector with our own version */
jQuery.find.selectors.filters.focus = function(elem) {
    var doc = elem.ownerDocument;
    return elem === doc.activeElement && !!(elem.type || elem.href);
}