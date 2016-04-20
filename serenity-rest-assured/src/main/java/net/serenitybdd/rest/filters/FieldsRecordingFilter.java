package net.serenitybdd.rest.filters;

import com.jayway.restassured.filter.Filter;
import com.jayway.restassured.filter.FilterContext;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.RequestLoggingFilter;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.FilterableRequestSpecification;
import com.jayway.restassured.specification.FilterableResponseSpecification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * User: YamStranger
 * Date: 4/15/16
 * Time: 7:40 AM
 */
public class FieldsRecordingFilter implements Filter {
    private final LogDetail logDetail;
    private final boolean shouldPrettyPrint;
    private String recorded = "";

    public FieldsRecordingFilter(final boolean shouldPrettyPrint, final LogDetail detail) {
        this.logDetail = detail;
        this.shouldPrettyPrint = shouldPrettyPrint;
    }

    @Override
    public Response filter(final FilterableRequestSpecification requestSpec,
                           final FilterableResponseSpecification responseSpec, final FilterContext ctx) {

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             PrintStream recordingStream = new PrintStream(output, true, StandardCharsets.UTF_8.toString())) {
            final RequestLoggingFilter filter = new RequestLoggingFilter(this.logDetail,
                    shouldPrettyPrint, recordingStream);
            final Response response = filter.filter(requestSpec, responseSpec, ctx);
            recordingStream.flush();
            this.recorded = new String(output.toByteArray(), StandardCharsets.UTF_8);
            this.recorded = this.recorded.replaceAll("^(" +
                    "(Proxy:)|(Body:)|(Cookies:)|(Headers:)|(Multiparts:)|(Request path:)" +
                    ")\\s*\\n*", "");
            this.recorded = this.recorded.replaceAll("^(<none>)", "");
            this.recorded = this.recorded.replaceAll("\n$", "");
            return response;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Incorrect implementation, should be used correct charset", e);
        } catch (IOException e) {
            throw new RuntimeException("Some exception during recording fields", e);
        }
    }

    public LogDetail logDetail() {
        return this.logDetail;
    }

    public String recorded() {
        return this.recorded;
    }
}
