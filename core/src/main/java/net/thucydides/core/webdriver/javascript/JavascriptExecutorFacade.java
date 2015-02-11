package net.thucydides.core.webdriver.javascript;

import com.beust.jcommander.internal.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.thucydides.core.pages.injectors.FieldInjectorService;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.reflection.FieldSetter;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

/**
 * Simple encapsulation of Javascript execution.
 */
public class JavascriptExecutorFacade {
    private WebDriver driver;

    private Map<Type, Object> typeAdapters = Maps.newHashMap();

    public JavascriptExecutorFacade(final WebDriver driver) {
        this();
        this.driver = driver;
    }

    public JavascriptExecutorFacade() {
    }

    protected Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        for(Type type : typeAdapters.keySet()) {
            builder.registerTypeAdapter(type, typeAdapters.get(type));
        }
        return builder.create();
    }


    /**
     * Register a GSon type adaptor to use during the JSON deserialization
     * @param type
     * @param typeAdapter
     */
    public void registerTypeAdapter(Type type, Object typeAdapter) {
        typeAdapters.put(type, typeAdapter);
    }

    /**
     * Execute some Javascript in the underlying WebDriver driver.
     * @param script
     */
    public Object executeScript(final String script) {
        if (javascriptIsSupportedIn(driver)) {
            JavascriptExecutor js = getJavascriptEnabledDriver();
            return js.executeScript(script);
        } else {
            return null;
        }
    }

    public Object executeScript(final String script, final Object... params) {
        if (javascriptIsSupportedIn(driver)) {
            JavascriptExecutor js = getJavascriptEnabledDriver();
            return js.executeScript(script, params);
        } else {
            return null;
        }
    }
    
    private String executeAndGetJsonAsString(final String script, final Object... params){
    	JQueryEnabledPage jQueryEnabledPage = JQueryEnabledPage.withDriver(getRealDriver());
        jQueryEnabledPage.injectJavaScriptUtils();
    	return (String)executeScript("return JSON.stringify(JSON.decycle(function(arguments){"+ script + "}(arguments)));", params);
    }    
    
    private <T> T deserializeJsonAs(Class<T> classOfT, final String objString){
        return getGson().fromJson(objString, classOfT);
    }
    
    private <T> List<T> deserializeJsonAsListOf(final String objString){
        Type listOfT = new TypeToken<List<T>>(){}.getType();
        return getGson().fromJson(objString, listOfT);
    }


    public <T> T deserializeScriptResultAs(Class<T> classOfT,
                                           final String script,
                                           final Map<String, Object> injectedFields,
                                           final Object... params) throws IllegalAccessException {
        String objString = executeAndGetJsonAsString(script, params);
        return (objString == null) ? null : injectParametersInto(deserializeJsonAs(classOfT, objString), injectedFields);
    }

    private final static Map<String, Object> NO_INJECTABLE_FIELDS = Maps.newHashMap();

    /**
     * Executes the JavaScript code and deserializes the resulting object as a classOfT.
     * 
     * @param classOfT Java Class
     * @param script that returns JavaScript Object
     * @param params a map of parameters to inject into the deserialized object
     * @return deserialized as classOfT object
     */
    public <T> T deserializeScriptResultAs(Class<T> classOfT, final String script, final Object... params) throws IllegalAccessException {
    	String objString = executeAndGetJsonAsString(script, params);
    	return (objString == null) ? null : injectParametersInto(deserializeJsonAs(classOfT, objString), NO_INJECTABLE_FIELDS);
    }

    private <T> T injectParametersInto(T t, Map<String, Object> params) throws IllegalAccessException {
        for(String fieldName : params.keySet()) {
            Object value = params.get(fieldName);
            FieldUtils.writeField(t, fieldName, value, true);
        }
        return t;
    }

    /**
     * Executes the JavaScript code and deserializes the resulting object as a List of <T>.
     * 
     * @param script that returns JavaScript Object
     * @return deserialized as List of classOfT
     */
    public <T> List<T> deserializeScriptResultAsListOf(final String script, final Object... params){
    	String objString = executeAndGetJsonAsString(script, params);
    	if (objString == null)
    		return null;
    	return deserializeJsonAsListOf(objString);
    }

    private WebDriver getRealDriver() {
        if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
            WebDriverFacade driverFacade = (WebDriverFacade) driver;
            return driverFacade.getProxiedDriver();
        } else {
            return driver;
        }
    }

    private JavascriptExecutor getJavascriptEnabledDriver() {
        return (JavascriptExecutor) getRealDriver();
    }

}
