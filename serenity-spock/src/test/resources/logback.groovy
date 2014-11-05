import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.classic.Level.WARN

root(WARN, ["CONSOLE"])

logger("net.thucydides", INFO, ["CONSOLE"])
logger("org.hibernate", WARN, ["CONSOLE"])