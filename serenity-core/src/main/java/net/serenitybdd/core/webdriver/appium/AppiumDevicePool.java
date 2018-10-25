package net.serenitybdd.core.webdriver.appium;

import com.google.common.base.Splitter;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.APPIUM_DEVICE_NAME;
import static net.thucydides.core.ThucydidesSystemProperty.APPIUM_DEVICE_NAMES;

/**
 * Manage Appium servers for multiple devices.
 * Used for parallel testing of Appium.
 *
 */
public class AppiumDevicePool {

    private final int DEFAULT_APPIUM_PORT = 4273;

    Logger LOGGER = LoggerFactory.getLogger(AppiumDevicePool.class);

    private final List<String> deviceList;
    private final List<String> availableDevices;
    private final Map<String, Integer> ports = new HashMap<>();

    private static AppiumDevicePool pool;

    public synchronized static AppiumDevicePool instance(EnvironmentVariables environmentVariables) {
        if (pool == null) {
            pool = new AppiumDevicePool(environmentVariables);
        }
        return pool;
    }

    public synchronized static AppiumDevicePool instance() {
        return instance(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public boolean hasMultipleDevices() {
        return deviceList.size() > 1;
    }

    public boolean hasOnlyOneDevice() {
        return deviceList.size() == 1;
    }

    public static void clear() {
        pool = null;
    }

    public AppiumDevicePool(EnvironmentVariables environmentVariables) {

        List<String> specifiedDevices = elementsIn(APPIUM_DEVICE_NAMES.from(environmentVariables, ""));

        String definedDevice = APPIUM_DEVICE_NAME.from(environmentVariables);

        if (specifiedDevices.isEmpty()) {
            deviceList = deviceListFromSingleDevice(definedDevice);
            ports.put(definedDevice,DEFAULT_APPIUM_PORT);
        } else {
            deviceList = elementsIn(APPIUM_DEVICE_NAMES.from(environmentVariables, ""));
        }

        availableDevices = Collections.synchronizedList(new ArrayList<>(deviceList));

        LOGGER.info("Appium Device Pool initialised with devices: " + deviceList);
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
        LOGGER.info("Device freed: " + deviceName);
        availableDevices.add(deviceName);
    }

    public synchronized String requestDevice() {
        if (availableDevices.isEmpty()) {
            throw new NoAvailableDeviceException("No available Appium device found - have you specified a device in appium.deviceName or a list of available devices in appium.deviceNames?");
        }
        String providedDevice = availableDevices.remove(0);
        LOGGER.info("Device provided: " + providedDevice);
        LOGGER.info("Remaining devices: " + availableDevices);

        return providedDevice;
    }

    public int portFor(String deviceName) {
        return ports.getOrDefault(deviceName, DEFAULT_APPIUM_PORT);
    }
}
