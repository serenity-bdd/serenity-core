package net.serenitybdd.rest;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.lowerCase;

/**
 * User: YamStranger
 * Date: 4/16/16
 * Time: 2:22 PM
 */
public enum DecomposedContentType {
    ANY("*/*", ContentType.ANY),
    TEXT("text/plain", ContentType.TEXT),
    APPLICATION_JSON("application/json", ContentType.JSON),
    APPLICATION_JAVASCRIPT("application/javascript", ContentType.JSON),
    APPLICATION_XML("application/xml", ContentType.XML),
    TEST_XML("text/xml", ContentType.XML),
    APPLICATION_XHTML_XML("application/xhtml+xml", ContentType.XML),
    TEST_JAVASCRIPT("text/javascript", ContentType.JSON),
    HTML("text/html", ContentType.HTML),
    URLENC("application/x-www-form-urlencoded", ContentType.URLENC),
    BINARY("application/octet-stream", ContentType.BINARY);

    private String representation;
    private ContentType type;
    private static Map<String, DecomposedContentType> declared;

    DecomposedContentType(final String representation, final ContentType type) {
        this.representation = representation;
        this.type = type;
        register(representation, this);
    }

    private static void register(final String representation, final DecomposedContentType decomposedContentType) {
        if (declared == null) {
            declared = new HashMap();
        }
        declared.put(normalize(representation), decomposedContentType);
    }

    private static String normalize(final String value) {
        return lowerCase(ObjectUtils.firstNonNull(value, "").replaceAll("\\s*", ""));
    }

    public DecomposedContentType byString(final String string) {
        final String normalized = normalize(ObjectUtils.firstNonNull(string, ""));
        DecomposedContentType decomposedContentType = declared.get(normalized);
        if (decomposedContentType == null) {
            for (final Map.Entry<String, DecomposedContentType> contentTypeEntry : declared.entrySet()) {
                //content type can contains encoding, this method should find it
                if (normalized.contains(contentTypeEntry.getKey())) {
                    decomposedContentType = contentTypeEntry.getValue();
                }
            }
        }
        return decomposedContentType;
    }

    @Override
    public String toString() {
        return this.representation;
    }

    public String asString() {
        return this.representation;
    }

    public ContentType contentType() {
        return this.type;
    }
}
