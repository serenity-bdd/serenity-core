package net.serenitybdd.core.environment

import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenDefiningBaseUrlsForDifferentEnvironments extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def "if no environments are defined, the normal base url will be used"() {
        given:
        environmentVariables.setProperty("webdriver.base.url", "http://foo.com")
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://foo.com"
    }

    def """different environment configurations can be defined using the "environment" system property.
             environments {
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "environment"                        : "dev",
                "environments.dev.webdriver.base.url": "http://dev.foo.com"
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://dev.foo.com"

    }


    def """If a default environment is defined, it will be used if no environment is set
             environments {
                default {
                    webdriver.base.url = http://default.myapp.myorg.com
                }
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.webdriver.base.url": "http://default.foo.com",
                "environments.dev.webdriver.base.url"    : "http://dev.foo.com"
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://default.foo.com"

    }


    def """If a environment property is not defined in an environment, we fall back on the default
             environments {
                default {
                    webdriver.base.url = http://default.myapp.myorg.com
                }
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.webdriver.base.url": "http://default.foo.com",
                "environments.dev.some.other.property"   : "some.other.value",
                "environment"                            : "dev",
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://default.foo.com"
    }

    def "If a environment property is not defined anywhere, a UndefinedEnvironmentVariableException should be thrown"() {

        given:
        environmentVariables.setProperties([
                "environments.dev.webdriver.base.url": "http://default.foo.com",
                "environment"                        : "dev",
        ])
        when:
        EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("unknown.property")
        then:
        thrown(UndefinedEnvironmentVariableException)
    }


    def """The special 'all' environment will work for any environment, and can be used with variable substitution from previous sections.
        For environment configuration variable substitution, we sue the # symbol to avoid conflicts with the TypeSafe variable substitution.
        environments {
            default {
                webdriver.base.url = http://localhost:8080/myapp
                accounts.service.url = http://localhost:8081
            }
            dev {
                webdriver.base.url = http://dev.myapp.myorg.com
                accounts.service.url = http://dev.accounts.myorg.com
            }
            staging {
                webdriver.base.url = http://staging.myapp.myorg.com
                accounts.service.url = http://staging.accounts.myorg.com
            }
            all {
                home.url = #{webdriver.base.url}/home
                config.url = #{webdriver.base.url}/config
                accounts.url = #{accounts.service.url}/accounts
            }
        }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.webdriver.base.url": "http://default.foo.com",
                "environments.dev.webdriver.base.url"    : "http://dev.foo.com",
                "environment"                            : "dev",
                "environments.all.home.url"              : "#{webdriver.base.url}/home",
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("home.url")
        then:
        baseUrl == "http://dev.foo.com/home"

    }

    def """The special 'all' environment variable can be built based on nested variables substitution from previous sections.
        For environment configuration variable substitution, we sue the # symbol to avoid conflicts with the TypeSafe variable substitution.
        environments {
            default {
                db.host = 5432
                db.port = localhost
                db.jdbc = jdbc:postgres://#{db.host}:#{db.port}
                db.name = my_db
            }

            test {
                db.host = 127.0.0.1
                db.port = 5432
                db.jdbc = jdbc:postgres://#{db.host}:#{db.port}
                db.name = your_db
            }

            all {
                db.connection.url = #{db.jdbc}/#{db.name}
            }
        }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.db.host"      : "localhost",
                "environments.default.db.port"      : "5432",
                "environments.default.db.jdbc"      : "jdbc:postgres://#{db.host}:#{db.port}",
                "environments.default.db.name"      : "my_db",
                "environments.test.db.host"         : "127.0.0.1",
                "environments.test.db.port"         : "5432",
                "environments.test.db.jdbc"         : "jdbc:postgres://#{db.host}:#{db.port}",
                "environments.test.db.name"         : "your_db",
                "environment"                       : "test",
                "environments.all.db.connection.url": "#{db.jdbc}/#{db.name}"
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("db.connection.url")
        then:
        baseUrl == "jdbc:postgres://127.0.0.1:5432/your_db"
    }

    def """The special 'all' environment variable can be built based on multi variable substitution from previous sections.
        For environment configuration variable substitution, we sue the # symbol to avoid conflicts with the TypeSafe variable substitution.
        environments {
            default {
                db.jdbc = jdbc:postgres://localhost:5432
                db.name = my_db
            }

            test {
                db.jdbc = jdbc:postgres://127.0.0.1:5432
                db.name = your_db
            }

            all {
                db.connection.url = #{db.jdbc}/#{db.name}
            }
        }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.db.jdbc"      : "jdbc:postgres://localhost:5432",
                "environments.default.db.name"      : "my_db",
                "environments.test.db.jdbc"         : "jdbc:postgres://127.0.0.1:5432",
                "environments.test.db.name"         : "your_db",
                "environment"                       : "test",
                "environments.all.db.connection.url": "#{db.jdbc}/#{db.name}"
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("db.connection.url")
        then:
        baseUrl == "jdbc:postgres://127.0.0.1:5432/your_db"
    }


    def """The 'all' environment properties work for any Serenity property"() For environment configuration variable substitution, we sue the # symbol to avoid conflicts with the TypeSafe variable substitution.
        default {
                base.host = "http://localhost"
                base.port = "8080"
        }
        all {
                serenity.project.name = "Default Application"
                serenity.maintain.session = "true"
        }
        local {
                base.host = "http://localhost"
                base.port = "8881"
        }
        qa {
                base.host = "http://ci-host"
                base.port = ""
        }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.base.host"            : "http://localhost",
                "environments.default.base.port"            : "8080",
                "environments.all.serenity.project.name"    : "Default Application",
                "environments.all.serenity.maintain.session": "true",
                "environments.local.base.host"              : "http://localhost",
                "environments.local.base.port"              : "8081",
                "environments.qa.base.host"                 : "http://qa-host",
                "environments.qa.base.port"                 : ""
        ])
        when:
        def maintainSession = ThucydidesSystemProperty.SERENITY_MAINTAIN_SESSION.booleanFrom(environmentVariables, false)
        then:
        maintainSession == expectedMaintainSessionValue
        where:
        environment | expectedMaintainSessionValue
        ""          | true
        "qa"        | true


    }

    def """If no configured environment can be found, the default should be used if defined
             environments {
                default {
                    webdriver.base.url = http://default.myapp.myorg.com
                }
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.default.webdriver.base.url": "http://default.foo.com",
                "environment"                            : "prod",
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://default.foo.com"

    }


    def """If no configured environment can be found, the normal value should be used if no default is defined
             environments {
                default {
                    webdriver.base.url = http://default.myapp.myorg.com
                }
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "webdriver.base.url": "http://default.foo.com",
                "environment"       : "prod",
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "http://default.foo.com"

    }

    def """If environments are configured but no environment is set, normal properties will be used
             environments {
                dev {
                    webdriver.base.url = http://dev.myapp.myorg.com
                }
        """() {

        given:
        environmentVariables.setProperties([
                "environments.dev.webdriver.base.url"  : "http://dev.myapp.myorg.com",
                "webdriver.base.url"                   : "a.normal.property"
        ])
        when:
        def baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url")
        then:
        baseUrl == "a.normal.property"
    }

    def "If no configured environment can be found but a property is present in the normal properties, this will be used"() {

        given:
        environmentVariables.setProperties([
                "favorite.color": "RED"
        ])
        when:
        def color = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("favorite.color")
        then:
        color.get() == "RED"
    }


    def "Optional properties do not throw an exception if not found"() {

        given:
        environmentVariables.setProperties([
                "favorite.color": "RED"
        ])
        when:
        def number = EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty("favorite.number")
        then:
        !number.isPresent()
    }


    def "Can list all the properties for a specified environment"() {

        given:
        environmentVariables.setProperties([
                "environment" : "dev",
                "favorite.color": "RED",
                "environments.dev.favorite.number" : "7",
                "environments.prod.favorite.number" : "3"
        ])
        when:
        def config = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix("favorite.")
        then:
        config.stringPropertyNames().containsAll("favorite.number","favorite.color")
        and:
        EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("favorite.color") == "RED"
        EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("favorite.number") == "7"
    }


    def "Environement-specific properties override general ones"() {

        when:
        environmentVariables.setProperties([
                "environment" : "dev",
                "favorite.color": "RED",
                "favorite.number":"0",
                "environments.dev.favorite.number" : "7",
                "environments.prod.favorite.number" : "3"
        ])
        then:
        EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("favorite.number") == "7"
    }
}
