package net.serenitybdd.rest.utils;

import io.restassured.filter.log.LogDetail;
import io.restassured.internal.print.ResponsePrinter;
import io.restassured.response.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: YamStranger
 * Date: 4/20/16
 * Time: 1:25 AM
 */
public class RestResponseRecordingHelper {
    private final List<LogDetail> logDetail;
    private final boolean shouldPrettyPrint;
    private Set<String> blackListedHeaders;

    public RestResponseRecordingHelper(final boolean shouldPrettyPrint, final Set<String> blackListedHeaders, final LogDetail... details) {
        this.blackListedHeaders = blackListedHeaders;
        this.logDetail = new LinkedList<>();
        this.logDetail.addAll(Arrays.asList(details));
        this.shouldPrettyPrint = shouldPrettyPrint;
    }

    public Map<LogDetail, String> print(final Response response) {
        Map<LogDetail, String> result = new HashMap();
        if (response != null) {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                 PrintStream recordingStream = new PrintStream(output, true, StandardCharsets.UTF_8.toString())) {
                for (final LogDetail detail : logDetail) {
                    try {
                        ResponsePrinter.print(response, response.getBody(), recordingStream, detail, shouldPrettyPrint, blackListedHeaders);
                    } catch (NullPointerException e) {
                        //can be thrown if some field like cookies or headers are empty
                    }
                    recordingStream.flush();
                    String recorded = new String(output.toByteArray(), StandardCharsets.UTF_8);
                    output.reset();
                    recorded = recorded.replaceAll("^(" +
                            "(Proxy:)|(Body:)|(Cookies:)|(Headers:)|(Multiparts:)|(Request path:)" +
                            ")\\s*\\n*", "");
                    recorded = recorded.replaceAll("^(<none>)", "");
                    recorded = recorded.replaceAll("\n$", "");
                    result.put(detail, recorded);

                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Incorrect implementation, should be used correct charset", e);
            } catch (IOException e) {
                throw new RuntimeException("Some exception during recording fields", e);
            }
        }
        return result;
    }
}
