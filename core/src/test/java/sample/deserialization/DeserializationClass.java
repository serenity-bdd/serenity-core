package sample.deserialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeserializationClass {
	
	public String str;
	
	public DeserializationClass klass;
}
