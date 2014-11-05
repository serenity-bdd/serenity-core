package net.thucydides.core.reports.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import spock.lang.Specification

class WhenSerializingJsonObjects extends Specification {

    def serializationContext = Mock(JsonSerializationContext)
    def type = Mock(java.lang.reflect.Type)

    def "should serialize Throwables to JSON"() {
        given:
            def adaptor = new ThrowableClassAdapter()
            def anException = new AssertionError("Oh bother")
        when:
            JsonElement element = adaptor.serialize(anException, type, serializationContext);
        then:
        ((JsonObject) element).get("class").toString() == "\"java.lang.AssertionError\""
        ((JsonObject) element).get("message").toString() == "\"Oh bother\""
    }

    def "should serialize Throwables without a message to JSON"() {
        given:
            def adaptor = new ThrowableClassAdapter()
            def anException = new AssertionError()
        when:
            JsonElement element = adaptor.serialize(anException, type, serializationContext);
        then:
            ((JsonObject) element).get("class").toString() == "\"java.lang.AssertionError\""
    }

}