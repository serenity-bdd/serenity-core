package net.thucydides.core.webdriver.stubs;

import io.appium.java_client.ExecutesMethod;
import org.openqa.selenium.remote.Response;

import java.util.Map;

public class ExecutesMethodStub implements ExecutesMethod {
    @Override
    public Response execute(String s, Map<String, ?> map) {
        return new Response();
    }

    @Override
    public Response execute(String s) {
        return new Response();
    }
}
