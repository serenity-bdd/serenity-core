package net.serenitybdd.core.webdriver.appium;

import com.google.common.base.Splitter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.thucydides.core.ThucydidesSystemProperty.APPIUM_DEVICE_NAME;
import static net.thucydides.core.ThucydidesSystemProperty.APPIUM_DEVICE_NAMES;

public class AppiumDevicePool {
    private final EnvironmentVariables environmentVariables;

    private final List<String> deviceList;
    private final List<String> availableDevices;

    private static AppiumDevicePool pool;

    public static AppiumDevicePool instance() {
        if (pool == null) {
            pool = new AppiumDevicePool(Injectors.getInjector().getInstance(EnvironmentVariables.class));
        }
        return pool;
    }

    public AppiumDevicePool(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;

        List<String> specifiedDevices = elementsIn(APPIUM_DEVICE_NAMES.from(environmentVariables, ""));

        String definedDevice = APPIUM_DEVICE_NAME.from(environmentVariables);

        deviceList = (specifiedDevices.isEmpty()) ?
                deviceListFromSingleDevice(definedDevice) :
                elementsIn(APPIUM_DEVICE_NAMES.from(environmentVariables, ""));

        availableDevices = Collections.synchronizedList(new ArrayList<>(deviceList));
    }

    private List<String> deviceListFromSingleDevice(String definedDevice) {
        return (definedDevice == null) ? new ArrayList<>() : Collections.singletonList(definedDevice);
    }

    public List<String> getAvailableDevices() {
        return new ArrayList<>(availableDevices);
    }

    private List<String> elementsIn(String list) {
        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(list);
    }

    public synchronized void freeDevice(String deviceName) {
        availableDevices.add(deviceName);
    }

    public synchronized String requestDevice() {
        if (availableDevices.isEmpty()) {
            throw new NoAvailableDeviceException();
        }
        return availableDevices.remove(0);
    }
}
