import ch.qos.logback.classic.encoder.PatternLayoutEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.WARN

def bySecond = timestamp("yyyyMMdd'T'HHmmss")

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    }
}

logger("net.thucydides", DEBUG)
logger("net.serenitybdd", DEBUG)
logger("org.hibernate", WARN)
logger("org.littleshoot.proxy", WARN)
root(WARN, ["STDOUT"])
