package sample.deserialization;

import com.fasterxml.jackson.annotation.JacksonInject;

public class DeserializationWithInjection {
	
	@JacksonInject("injected")
	public String injectable;

}
