import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN

def bySecond = timestamp("yyyyMMdd'T'HHmmss")

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

logger("net.thucydides", INFO)
logger("net.serenitybdd", INFO)
logger("org.hibernate", WARN)
root(WARN, ["STDOUT"])
