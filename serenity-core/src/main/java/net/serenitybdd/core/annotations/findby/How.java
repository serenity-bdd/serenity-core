package net.serenitybdd.core.annotations.findby;

public enum How {
    CLASS_NAME,
    CSS,
    ID,
    ID_OR_NAME,
    LINK_TEXT,
    NAME,
    PARTIAL_LINK_TEXT,
    TAG_NAME,
    XPATH,
    SCLOCATOR,
    JQUERY,
    ACCESSIBILITY_ID,
    ANDROID_UI_AUTOMATOR,
    /**
     * Deprecated - no longer supported by Appium
     */
    IOS_UI_AUTOMATION,
    IOS_CLASS_CHAIN,
    IOS_NS_PREDICATE_STRING
}
