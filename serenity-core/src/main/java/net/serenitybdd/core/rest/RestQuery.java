package net.serenitybdd.core.rest;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

public class RestQuery {

    private final RestMethod method;
    private final String path;
    private final String content;
    private final String contentType;
    private final Optional<? extends List<Object>> parameters;
    private final Optional<? extends Map<String, ?>> parameterMap;
    private final String responseBody;
    private final Integer statusCode;

    private RestQuery(RestMethod method, String path, List<Object> parameters, Map<String, ?> parameterMap, String content, String contentType, String responseBody, Integer statusCode) {
        this.method = method;
        this.path = path;
        this.parameters = Optional.fromNullable(parameters);
        this.parameterMap = Optional.fromNullable(parameterMap);
        this.content = content;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }

    public RestQuery(RestMethod method, String path) {
        this(method, path, null, null, null, null, null, null);
    }


    public RestQuery withParameters(List<Object> parameters) {
        return new RestQuery(method, path, parameters, null, content, contentType, responseBody, statusCode);
    }

    public RestQuery withParameters( Map<String, ?> parameterMap) {
        return new RestQuery(method, path, null, parameterMap, content, contentType, responseBody, statusCode);
    }

    public RestQuery withResponse(String responseBody) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType, responseBody, statusCode);
    }

    public RestQuery withStatusCode(Integer statusCode) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType, responseBody, statusCode);
    }

    public RestQuery withContent(String content) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType, responseBody, statusCode);
    }

    public RestQuery withContentType(String contentType) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType, responseBody, statusCode);
    }

    public RestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Optional<? extends List<Object>> getParameters() {
        return parameters;
    }

    public Optional<? extends Map<String, ?>> getParameterMap() {
        return parameterMap;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public boolean hasParameters() {
        return parameters.isPresent() || parameterMap.isPresent();
    }

    public String toString() {
        return getFormattedQuery();
    }

    public String getFormattedQuery() { return getMethod() + " " + injectParametersInto(getPath()); }

    private String injectParametersInto(String path) {
        if (parameters.isPresent()) {
            return injectParameterValuesInto(path, parameters.get());
        } else if (parameterMap.isPresent()) {
            return injectParameterMapInto(path, parameterMap.get());
        } else {
            return path;
        }
    }

    private String injectParameterMapInto(String path, Map<String, ?> parameterMap) {
        for(String parameterName : parameterMap.keySet()) {
            String parameterValue = parameterMap.get(parameterName).toString();
            path = path.replaceAll("\\{" + parameterName + "\\}", parameterValue);
        }
        return path;
    }

    private String injectParameterValuesInto(String path, List<Object> parameters) {

        for (Object parameterValue : parameters) {
            int variableStart = path.indexOf("{");
            int variableEnd = path.indexOf("}");
            if (variableStart >= 0 && variableEnd > variableStart) {
                path = path.substring(0, variableStart) + parameterValue + path.substring(variableEnd + 1);
            }
        }
        return path;
    }

    public static RestQueryBuilder withMethod(RestMethod method) {
        return new RestQueryBuilder(method);
    }

    public static class RestQueryBuilder {

        private RestMethod method;

        public RestQueryBuilder(RestMethod method) {
            this.method = method;
        }

        public RestQuery andPath(String path) {
            return new RestQuery(method, path);
        }
    }
}
