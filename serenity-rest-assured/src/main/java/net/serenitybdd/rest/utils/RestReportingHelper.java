package net.serenitybdd.rest.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.internal.support.Prettifier;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.model.rest.RestMethod;
import net.serenitybdd.model.rest.RestQuery;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;
import net.serenitybdd.rest.event.RecordRestQueryEvent;
import net.serenitybdd.rest.filters.FieldsRecordingFilter;
import net.thucydides.core.steps.events.StepFailedEvent;
import net.thucydides.core.steps.events.StepStartedEvent;
import net.thucydides.core.steps.session.TestSession;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.thucydides.core.steps.StepEventBus.getEventBus;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;


/**
 * User: YamStranger
 * Date: 4/6/16
 * Time: 7:51 AM
 */
public class RestReportingHelper {

    final int MAX_LOGGABLE_BODY_SIZE = 16 * 1024;

    private static boolean shouldRecordResponseBodyFor(Response result) {
        final ContentType type = ContentType.fromContentType(result.contentType());
        return type != null && (ContentType.JSON == type || ContentType.XML == type
                || ContentType.TEXT == type || ContentType.HTML == type);
    }

    public RestQuery recordRestSpecificationData(final RestMethod method,
                                                 final RequestSpecificationDecorated spec,
                                                 final String path,
                                                 final Object... params) {

        final Map<LogDetail, String> values = new HashMap<>();
        for (final Filter filter : spec.getDefinedFilters()) {
            if (filter instanceof FieldsRecordingFilter) {
                final FieldsRecordingFilter internal = (FieldsRecordingFilter) filter;
                values.put(internal.logDetail(), internal.recorded());
            }
        }

        String contentType = spec.getContentType();
        boolean isBinary = isBinaryContentType(contentType);
        String body = firstNonNull(values.get(LogDetail.BODY), "");


        if (isBinary || body.length() > MAX_LOGGABLE_BODY_SIZE) {
            body = "[Body content omitted due to binary or size constraints]";
        }

        return RestQuery
                .withMethod(method)
                .andPath(ObjectUtils.firstNonNull(
                        values.get(LogDetail.URI).replaceFirst("^Request URI:\t", ""), ""))
                .withContentType(String.valueOf(ContentType.fromContentType(contentType)))
                .withContent(body)
                .withRequestCookies(firstNonNull(values.get(LogDetail.COOKIES), ""))
                .withRequestHeaders(firstNonNull(values.get(LogDetail.HEADERS), ""));
    }

    private boolean isBinaryContentType(String contentType) {
        if (contentType == null) return false;
        return contentType.startsWith("application/octet-stream") ||
                contentType.startsWith("application/pdf") ||
                contentType.startsWith("image/") ||
                contentType.startsWith("video/") ||
                contentType.startsWith("audio/");
    }

    public void registerCall(final RestMethod method, final Response response,
                             final RequestSpecificationDecorated spec,
                             final String path, final Object... params) {
        RestQuery restQuery = recordRestSpecificationData(method, spec, path, params);
        Set<String> blackListedHeaders = spec.getConfig().getLogConfig().blacklistedHeaders();
        final RestResponseRecordingHelper helper = new RestResponseRecordingHelper(true, blackListedHeaders,
                LogDetail.HEADERS, LogDetail.COOKIES);
        final Map<LogDetail, String> values = helper.print(response);
        if (shouldRecordResponseBodyFor(response)) {
            String renderedBody = new Prettifier().getPrettifiedBodyIfPossible(
                    (ResponseOptions) response.getBody(), response.getBody());

            restQuery = restQuery.withResponse(renderedBody.isEmpty() ? response.asString() : renderedBody);
        }
        restQuery = restQuery.withStatusCode(response.getStatusCode())
                .withResponseHeaders(firstNonNull(values.get(LogDetail.HEADERS), ""))
                .withResponseCookies(firstNonNull(values.get(LogDetail.COOKIES), ""));
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new RecordRestQueryEvent(restQuery));
        } else {
            getEventBus().getBaseStepListener().recordRestQuery(restQuery);
        }

    }

    public void registerCall(final RestMethod method, final RequestSpecificationDecorated spec, final String path,
                             final RuntimeException throwable, final Object... params) {
        RestQuery restQuery = recordRestSpecificationData(method, spec, path, params);
        ExecutedStepDescription description = ExecutedStepDescription.withTitle(restQuery.toString());
        Throwable exception;
        if (throwable instanceof RestRuntimeException) {
            exception = throwable.getCause();
        } else {
            exception = throwable;
        }
        StepFailure failure = new StepFailure(description, exception);
        if (TestSession.isSessionStarted()) {
            TestSession.addEvent(new StepStartedEvent(description));
            TestSession.addEvent(new RecordRestQueryEvent(restQuery));
             List<ScreenshotAndHtmlSource> screenshotList = TestSession.getTestSessionContext().getStepEventBus().takeScreenshots(TestResult.FAILURE);
            TestSession.addEvent(new StepFailedEvent(failure,screenshotList));
        } else {
            getEventBus().stepStarted(description);
            getEventBus().getBaseStepListener().recordRestQuery(restQuery);
            getEventBus().stepFailed(failure);
        }
        if (Serenity.shouldThrowErrorsImmediately()) {
            throw throwable;
        }
    }
}
