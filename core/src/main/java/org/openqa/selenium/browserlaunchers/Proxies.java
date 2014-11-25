package org.openqa.selenium.browserlaunchers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
public class Proxies {
    public static Proxy extractProxy(Capabilities capabilities) {
        return Proxy.extractFrom(capabilities);
    }
}