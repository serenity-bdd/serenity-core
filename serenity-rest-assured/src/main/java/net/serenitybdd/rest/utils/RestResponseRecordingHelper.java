package net.serenitybdd.rest.utils;

import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.internal.print.ResponsePrinter;
import com.jayway.restassured.response.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * User: YamStranger
 * Date: 4/20/16
 * Time: 1:25 AM
 */
public class RestResponseRecordingHelper {
    private final List<LogDetail> logDetail;
    private final boolean shouldPrettyPrint;

    public RestResponseRecordingHelper(final boolean shouldPrettyPrint, final LogDetail... details) {
        this.logDetail = new LinkedList<>();
        this.logDetail.addAll(Arrays.asList(details));
        this.shouldPrettyPrint = shouldPrettyPrint;
    }

    public Map<LogDetail, String> print(final Response response) {
        Map<LogDetail, String> result = new HashMap<>();
        if (response != null) {
            try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                 PrintStream recordingStream = new PrintStream(output, true, StandardCharsets.UTF_8.toString())) {
                for (final LogDetail detail : logDetail) {
                    try {
                        ResponsePrinter.print(response, response.getBody(), recordingStream, detail, shouldPrettyPrint);
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