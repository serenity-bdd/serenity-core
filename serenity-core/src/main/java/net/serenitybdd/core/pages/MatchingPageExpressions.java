package net.serenitybdd.core.pages;

import net.thucydides.core.annotations.At;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Determines which URLs a given page object will work with.
 */
public class MatchingPageExpressions {

    private final List<Pattern> matchingExpressions = new ArrayList<Pattern>();

    private static final Map<String, String> MACROS = new HashMap<String, String>();

    static {
        MACROS.put("#HOST", "https?://[^/]+");
    }

    private static final String OPTIONAL_PARAMS = "/?(\\?.*)?";

    public MatchingPageExpressions(final PageObject pageObject) {
        buildMatchingExpressionsList(pageObject);
    }

    private void buildMatchingExpressionsList(final PageObject pageObject) {
        At compatibleWithAnnotation = pageObject.getClass().getAnnotation(At.class);
        if (compatibleWithAnnotation != null) {
            if (valueIsDefinedFor(compatibleWithAnnotation)) {
                worksWithUrlPattern(compatibleWithAnnotation.value());
            } else {
                worksWithUrlPatternList(compatibleWithAnnotation.urls());
            }
        }
    }

    public boolean isEmpty() {
        return matchingExpressions.isEmpty();
    }

    public boolean matchUrlAgainstEachPattern(final String currentUrl) {
        boolean pageWorksHere = false;
        for (Pattern pattern : matchingExpressions) {
            if (urlIsCompatibleWithThisPattern(currentUrl, pattern)) {
                pageWorksHere = true;
                break;
            }
        }
        return pageWorksHere;
    }


    private boolean urlIsCompatibleWithThisPattern(final String currentUrl,
                                                   final Pattern pattern) {
        return pattern.matcher(currentUrl).matches();
    }

    private void worksWithUrlPatternList(final String[] urls) {
        for (String url : urls) {
            worksWithUrlPattern(url);
        }
    }

    private boolean valueIsDefinedFor(final At compatibleWithAnnotation) {
        return ((compatibleWithAnnotation.value() != null) && (compatibleWithAnnotation
                .value().length() > 0));
    }

    private void worksWithUrlPattern(final String urlPattern) {
        String processedUrlPattern = substituteMacrosIn(urlPattern);
        matchingExpressions.add(Pattern.compile(processedUrlPattern));
    }

    private String substituteMacrosIn(final String urlPattern) {
        String patternWithExpandedMacros = urlPattern;
        for (String macro : MACROS.keySet()) {
            String expanded = MACROS.get(macro);
            patternWithExpandedMacros = patternWithExpandedMacros.replaceAll(
                    macro, expanded);
        }
        return patternWithExpandedMacros + OPTIONAL_PARAMS;
    }

}
