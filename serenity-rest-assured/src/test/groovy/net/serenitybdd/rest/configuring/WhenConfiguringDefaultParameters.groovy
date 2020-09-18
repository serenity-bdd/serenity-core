package net.serenitybdd.rest.configuring

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.restassured.RestAssured
import io.restassured.authentication.BasicAuthScheme
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.config.RestAssuredConfig
import io.restassured.config.SessionConfig
import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.mapper.ObjectMapper
import io.restassured.mapper.ObjectMapperDeserializationContext
import io.restassured.mapper.ObjectMapperSerializationContext
import io.restassured.parsing.Parser
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import io.restassured.specification.ProxySpecification
import net.serenity.test.utils.rules.TestCase
import net.serenitybdd.rest.rules.RestConfigurationAction
import net.serenitybdd.rest.rules.RestConfigurationRule
import net.thucydides.core.steps.BaseStepListener
import org.hamcrest.Matchers
import org.junit.Rule
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import static net.serenitybdd.rest.SerenityRest.filters
import static net.serenitybdd.rest.SerenityRest.getDefaultAuthentication
import static net.serenitybdd.rest.SerenityRest.getDefaultBasePath
import static net.serenitybdd.rest.SerenityRest.getDefaultConfig
import static net.serenitybdd.rest.SerenityRest.getDefaultParser
import static net.serenitybdd.rest.SerenityRest.getDefaultPort
import static net.serenitybdd.rest.SerenityRest.getDefaultProxy
import static net.serenitybdd.rest.SerenityRest.getDefaultResponseSpecification
import static net.serenitybdd.rest.SerenityRest.getDefaultRootPath
import static net.serenitybdd.rest.SerenityRest.getDefaultSessionId
import static net.serenitybdd.rest.SerenityRest.given
import static net.serenitybdd.rest.SerenityRest.isUrlEncodingEnabled
import static net.serenitybdd.rest.SerenityRest.objectMapper
import static net.serenitybdd.rest.SerenityRest.replaceFiltersWith
import static net.serenitybdd.rest.SerenityRest.reset
import static net.serenitybdd.rest.SerenityRest.setDefaultAuthentication
import static net.serenitybdd.rest.SerenityRest.setDefaultBasePath
import static net.serenitybdd.rest.SerenityRest.setDefaultConfig
import static net.serenitybdd.rest.SerenityRest.setDefaultParser
import static net.serenitybdd.rest.SerenityRest.setDefaultPort
import static net.serenitybdd.rest.SerenityRest.setDefaultProxy
import static net.serenitybdd.rest.SerenityRest.setDefaultRequestSpecification
import static net.serenitybdd.rest.SerenityRest.setDefaultResponseSpecification
import static net.serenitybdd.rest.SerenityRest.setDefaultRootPath
import static net.serenitybdd.rest.SerenityRest.setDefaultSessionId
import static net.serenitybdd.rest.SerenityRest.setUrlEncodingEnabled

/**
 * User: YamStranger
 * Date: 3/14/16
 * Time: 9:57 AM
 */
class WhenConfiguringDefaultParameters extends Specification {

    @Rule
    def WireMockRule wire = new WireMockRule(0);

    @Rule
    def RestConfigurationRule rule = new RestConfigurationRule(new RestConfigurationAction() {
        @Override
        void apply() {
            reset()
        }
    },)

    @Rule
    def TestCase<BaseStepListener> test = new TestCase({
        Mock(BaseStepListener)
    }.call());

    def Gson gson = new GsonBuilder().setPrettyPrinting().
        serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();


    def "should be possible set base path for all rest requests"(final def String path
    ) {
        given: "rest assured base path updated"
            setDefaultBasePath(path)
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
        then: "default base path should be used for default request"
            getDefaultBasePath() == path
            request.getBasePath() == path
        where:
            path << ["/base",
                     "/secondBase"]
    }

    def "should be possible set default port for all rest requests"(final def Integer port
    ) {
        given: "rest assured default port updated"
            setDefaultPort(port)
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
        then: "default port should be used for created request"
            getDefaultPort() == port
            request.getPort() == port
        where:
            port << [74,
                     15,
                     85]
    }

    def "should be possible mark if url encoding enabled for all rest requests"(final def boolean isUrlEncodingEnbabled
    ) {
        given: "rest assured default rul encoding enabling flag updated"
            setUrlEncodingEnabled(isUrlEncodingEnbabled)
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
        then: "rest assured default value should be updated"
            isUrlEncodingEnabled() == isUrlEncodingEnbabled
            RestAssured.urlEncodingEnabled == isUrlEncodingEnbabled
        where:
            isUrlEncodingEnbabled << [true,
                                      false]
    }

    def "should be possible set root path for all rest requests"(final def String rootPath
    ) {
        given: "rest assured root path updated"
            setDefaultRootPath(rootPath)
        and: "mocked rest service"
            def body = "<$rootPath>" +
                "<value>1</value>" +
                "</$rootPath>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating request, and seeding some request"
            def request = (FilterableRequestSpecification) given().port(wire.port());
            def response = request.get(url)
        then: "default root path should be used for request validation"
            response.then().body("value", Matchers.equalTo("1"))
            getDefaultRootPath() == rootPath
        where:
            rootPath << ["root",
                         "secondRoot"]
    }

    def "should be possible set default session id for all rest requests"(final def String session
    ) {
        given: "rest assured default session id updated"
            setDefaultSessionId(session)
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
        then: "default session id should be used for created request"
            getDefaultSessionId() == session
            request.getConfig().getSessionConfig().sessionIdValue() == session
        where:
            session << ["first",
                        "Second"]
    }

    def "should be possible set default authentication for all rest requests"(
        final def String password,
        final def String login
    ) {
        given: "rest assured default authentication updated"
            def authentication = RestAssured.basic(login, password)
            setDefaultAuthentication(authentication)
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating request and processing"
            def specification = (FilterableRequestSpecification) given();
            def request = specification.options(url)
        then: "default authentication should be used for created request"
            getDefaultAuthentication() == authentication
            def scheme = (BasicAuthScheme) specification.getAuthenticationScheme()
            scheme == authentication
            scheme.getPassword() == password
            scheme.getUserName() == login
        where:
            password     | login
            "secret"     | "bond"
            "UI_DESTROY" | "Dark"
    }

    def "should be possible set default config for all rest requests"(
        final def String sessionId
    ) {
        given: "rest assured default config updated"
            def configuration = new RestAssuredConfig()
            configuration = configuration.sessionConfig(new SessionConfig(sessionId))
            setDefaultConfig(configuration)
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
            def response = request.options(url)
        then: "default config should be used for created request"
            getDefaultConfig().sessionConfig.sessionIdValue() == sessionId
            request.getConfig().sessionConfig.sessionIdValue() == sessionId
        where:
            sessionId << ["sessoinId1", "sessionId2"]
    }

    def "should be possible set default proxy specification for all rest requests"(
        final def ProxySpecification specification
    ) {
        given: "rest assured default proxy specification updated"
            setDefaultProxy(specification)
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
        then: "default proxy specification should be used for created request"
            getDefaultProxy() == specification
            request.getProxySpecification() == specification
        where:
            specification << [new ProxySpecification("exampleHost", 8095, "schema"),
                              new ProxySpecification("host2", 9846, "schema2")]
    }


    def "should be possible set default request specification to merge with all rest requests"(
        final def String password,
        final def String login
    ) {
        given: "rest assured default request specification updated"
            setDefaultRequestSpecification(
                new RequestSpecBuilder().setAuth(RestAssured.basic(login, password)).build())
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating request"
            def specification = (FilterableRequestSpecification) given();
            def request = specification.options(url)
        then: "default request specification should be merged with created request"
            def scheme = (BasicAuthScheme) specification.getAuthenticationScheme()
            scheme.getPassword() == password
            scheme.getUserName() == login
        where:
            password   | login
            "top"      | "rabit"
            "12354242" | "fox"
    }

    def "should be possible set default response specification to merge with all rest responses"(
        final def String rootPath
    ) {
        given: "rest assured default response specification updated"
            def specification = new ResponseSpecBuilder().rootPath(rootPath).build()
            setDefaultResponseSpecification(specification)
            def body = "<$rootPath>" +
                "<value>1</value>" +
                "</$rootPath>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating response"
            def response = given().get(url);
        then: "default response specification should be merged with created request"
            getDefaultResponseSpecification() == specification
            response.then().body("value", Matchers.equalTo("1"))
        where:
            rootPath << ["X",
                         "Y",
                         "po"]
    }

    def "should be possible set default parser to be used in all rest requests and responses"(
        final def Parser parser
    ) {
        given: "rest assured default parser specification updated"
            setDefaultParser(parser)
        when: "creating response"
            def response = given();
        then: "default parser should be accessable from rest assurance"
            getDefaultParser() == parser
            RestAssured.defaultParser
        where:
            parser << [Parser.JSON, Parser.XML]
    }

//    def "should be possible add custom default filter to be used during request/response creating"(
    def add_custom_default_filter(
        final def Filter filter
    ) {
        given: "rest assured default filters updated"
            def list = new ArrayList<Filter>()
            list.add(filter);
            filters(list)
        when: "getting filters"
            def defaults = RestAssured.filters()
        then: "default filters should be configured property"
            defaults.size() == 1
            defaults.contains(filter)
            filters().containsAll(defaults)
        where:
            filter << [new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }, new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }]
    }

    def add_custom_default_filter() {
        given: "rest assured default filters updated"
            def filter1 = new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }
            def filter2 = new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }
            filters(filter1, filter2)
        when: "getting filters"
            def defaults = RestAssured.filters()
        then: "default filters should be configured property"
            defaults.size() == 2
            defaults.contains(filter2) && defaults.contains(filter1)
            filters().containsAll(defaults)
    }

    def add_custom_default_filters() {
        given: "rest assured default filters updated"
            def filter1 = new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }
            def filter2 = new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }
            def replacing = new Filter() {
                @Override
                Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
                    throw new UnsupportedOperationException("#filter()")
                }
            }
            filters(filter1, filter2)
        when: "replacing filters and getting filters"
            replaceFiltersWith(replacing)
            def defaults = RestAssured.filters()
        then: "default filters should be configured property"
            defaults.size() == 1
            !defaults.contains(filter2) && !defaults.contains(filter1)
            filters().containsAll(defaults) && filters().size() == defaults.size()
    }

    def "should be possible set default object mapper for all rest requests"(
        final ObjectMapper mapper
    ) {
        given: "rest assured default config updated"
            objectMapper(mapper)
            def body = "<root>" +
                "<value>1</value>" +
                "</root>"

            def base = "http://localhost:${wire.port()}"
            def path = "/test/levels"
            def url = "$base$path"

            stubFor(WireMock.get(urlMatching(path))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/xml")
                .withBody(body)));
        when: "creating request"
            def request = (FilterableRequestSpecification) given();
            def response = request.options(url)
        then: "default config should be used for created request"
            request.getConfig().objectMapperConfig.defaultObjectMapper() == mapper
        where:
            mapper << [new ObjectMapper() {
                @Override
                Object deserialize(ObjectMapperDeserializationContext context) {
                    return "deserialized";
                }

                @Override
                Object serialize(ObjectMapperSerializationContext context) {
                    return null;
                }
            }]
    }
}
