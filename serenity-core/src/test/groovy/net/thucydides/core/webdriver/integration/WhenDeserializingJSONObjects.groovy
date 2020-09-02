package net.thucydides.core.webdriver.integration

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import net.thucydides.core.webdriver.javascript.JavascriptExecutorFacade
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import sample.deserialization.DeserializationClass
import sample.deserialization.DeserializationWithInjection
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Type

class WhenDeserializingJSONObjects extends Specification {
	
	@Shared
	WebDriver driver;

	@Shared
	JavascriptExecutorFacade jsFacade;
	
	def setupSpec() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		driver = new ChromeDriver(chromeOptions)
		jsFacade = new JavascriptExecutorFacade(driver)
	}

	def cleanupSpec() {
		if (driver) {
			driver.close()
			driver.quit()
		}
	}

		
	def "should deserialize JSON as proper object"() {
		given:"object exists in javascript"
			jsFacade.executeScript("obj = {str: 'Test deserialization'}")
		when:"we execute the script and deserialize its result"
			def obj = jsFacade.deserializeScriptResultAs(DeserializationClass.class, "return obj") 
		then:
		    obj.str.equals("Test deserialization")
	}

 	def "should deserialize cyclic JSON as properly but breaking the cyclic dependency"() {
		given:"cyclic object exists in javascript"
			jsFacade.executeScript("obj = {str: 'Test Cycle'}; obj.klass = obj;")
		when:"we execute the script and deserialize its result"
			def obj = jsFacade.deserializeScriptResultAs(DeserializationClass.class, "return obj")
		then:
			obj.str.equals("Test Cycle")
			obj.klass.getClass() == DeserializationClass.class
			obj.klass.str == null		 
	}

	@Ignore
	def "should deserialize JSON as proper object when arguments are passed to the script"() {
		given: "test arguments"
			def arg1 = "Test argument"
			def arg2 = "Test argument2"
		when: "deserialization method is called with script and arguments"
			def obj = jsFacade.deserializeScriptResultAs(DeserializationClass.class, 
				"return {str: arguments[0], klass : {str: arguments[1]}}",
				arg1, arg2)
		then: "both arguments were used"
			obj.str.equals(arg1)
			obj.klass.str.equals(arg2)
	}

	def "should return null if the JSON object is null"() {
		when:" object is null"
			def obj = jsFacade.deserializeScriptResultAs(DeserializationClass.class, "return null")
		then:
			obj == null
	}

	def "should return null if the JSON object is undefined"() {
		when:"object is undefined"
			def obj = jsFacade.deserializeScriptResultAs(DeserializationClass.class, "var o; return o;")
		then:
			obj == null
	}

	def "should throw an Exception when script has an error"() {
		when:"object is undefined"
			jsFacade.deserializeScriptResultAs(DeserializationClass.class, "rtrn o;")
		then:
			thrown(WebDriverException)
	}

	def "should deserialize JSON as proper list of object"() {
		when: "the script returns the array with objects"
			List<DeserializationClass> list = jsFacade.deserializeScriptResultAsListOf("return [{str: 'List1'}, {str: 'List2'}]")
		then: "list should have be proper size and have the deserialized objects"
			list.size() == 2
			list.get(0) != null
			list.get(0).str == "List1"
			list.get(1) != null
			list.get(1).str == "List2"
	}

	@Ignore
	def "should deserialize JSON as proper list of object when arguments are passed to the script"() {
		given: "test arguments"
			def arg1 = "Test argument for List"
			def arg2 = "Test argument2 for List"
		when:"the script with arguments returns the array with objects"
			List<DeserializationClass> list = jsFacade.deserializeScriptResultAsListOf("return [{str: arguments[0]}, {str: arguments[1]}];",
				arg1, arg2)
		then:"list should have be proper size and have the deserialized objects with correct values"
			list.size() == 2
			list.get(0) != null
			list.get(0).str == arg1
			list.get(1) != null
			list.get(1).str == arg2
	}

	def "should deserialize JSON as emply List if empty array is returned"() {
		when:"the script with arguments returns an empty array"
			List<DeserializationClass> list = jsFacade.deserializeScriptResultAsListOf("return [];")
		then:"list should be empty"
			list.isEmpty()
	}
	
	def "should return null if the JSON list object is null"() {
		when:" object is null"
			List<DeserializationClass> list = jsFacade.deserializeScriptResultAsListOf("return null;")
		then:
			list == null
	}
	
	def "should return null if the JSON list object is undefined"() {
		when:" object is undefined"
			List<DeserializationClass> list = jsFacade.deserializeScriptResultAsListOf("var something; return something;")
		then:
			list == null
	}

	def "should use custom type adapters if provided"() {
		given: "the custom mapper that deserializes String differently"
			JsonDeserializer deserializer = new JsonDeserializer<String>() {
				@Override
				String deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
					return "deserialized " + jsonElement.getAsJsonPrimitive().getAsString();
				}
			}
			jsFacade.registerTypeAdapter(String.class, deserializer)

		when:"we execute the script and deserialize its result"
			DeserializationClass obj = jsFacade.deserializeScriptResultAs(DeserializationClass, "return {str: 'text'};")
		then:
			obj.str.equals("deserialized text")
	}

	def "should inject values if injector is passed"() {
		when:"we execute the script and deserialize its result"
			DeserializationWithInjection obj = jsFacade.deserializeScriptResultAs(DeserializationWithInjection, "return {};",
																			   	  ["injectable": "Injected Value"])
		then:
			obj.injectable.equals("Injected Value")
	}


}
