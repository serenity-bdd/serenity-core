package net.thucydides.core.webdriver.javascript;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.thucydides.core.pages.jquery.JQueryEnabledPage;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.util.List;

import static net.thucydides.core.webdriver.javascript.JavascriptSupport.javascriptIsSupportedIn;

/**
 * Simple encapsulation of Javascript execution.
 */
public class JavascriptExecutorFacade {
    private WebDriver driver;
    private ObjectMapper mapper;
    private InjectableValues inject;
    
    public JavascriptExecutorFacade(final WebDriver driver) {
        this.driver = driver;
    }
    
    public JavascriptExecutorFacade withObjectMapper(ObjectMapper mapper){
    	this.mapper = mapper;
    	return this;
    }
    public JavascriptExecutorFacade withInjectableValues(InjectableValues inject){
    	this.inject = inject;
    	return this;
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
    	ObjectMapper mapper = getMapper();
    	ObjectReader reader = mapper.reader(classOfT);
    	if (inject != null){
    		reader = reader.with(inject);
    	}
    	try {
    		return reader.readValue(objString);
		} catch (JsonParseException e) {
			throw new WebDriverException(e);
		} catch (JsonMappingException e) {
			throw new WebDriverException(e);
		} catch (IOException e) {
			throw new WebDriverException(e);
		}
    }
    
    private <T> List<T> deserializeJsonAsListOf(Class<T> classOfT, final String objString){
    	ObjectMapper mapper = getMapper();
    	ObjectReader reader = mapper.reader(TypeFactory.defaultInstance().constructCollectionType(List.class, classOfT));
    	if (inject != null){
    		reader = reader.with(inject);
    	}
    	try {
    		return reader.readValue(objString);
		} catch (JsonParseException e) {
			throw new WebDriverException(e);
		} catch (JsonMappingException e) {
			throw new WebDriverException(e);
		} catch (IOException e) {
			throw new WebDriverException(e);
		}
    }

	private ObjectMapper getMapper(){
		if (mapper == null){
			mapper = new ObjectMapper();
		}
    	return mapper;
    }
    /**
     * Executes the JavaScript code and deserializes the resulting object as a classOfT.
     * 
     * @param classOfT Java Class
     * @param script that returns JavaScript Object
     * @param params for the script
     * @return deserialized as classOfT object
     */
    public <T> T deserializeScriptResultAs(Class<T> classOfT, final String script, final Object... params){
    	String objString = executeAndGetJsonAsString(script, params);
    	if (objString == null)
    		return null;
    	return deserializeJsonAs(classOfT, objString);
    }
    
    /**
     * Executes the JavaScript code and deserializes the resulting object as a List of classOfT.
     * 
     * @param classOfT Java Class to reflect on
     * @param script that returns JavaScript Object
     * @return deserialized as List of classOfT
     */
    public <T> List<T> deserializeScriptResultAsListOf(Class<T> classOfT, final String script, final Object... params){
    	String objString = executeAndGetJsonAsString(script, params);
    	if (objString == null)
    		return null;
    	return deserializeJsonAsListOf(classOfT, objString); 
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
