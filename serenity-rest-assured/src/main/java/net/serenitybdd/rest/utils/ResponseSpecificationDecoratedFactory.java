package net.serenitybdd.rest.utils;


import io.restassured.internal.ResponseSpecificationImpl;
import net.serenitybdd.rest.decorators.ResponseSpecificationDecorated;

public interface ResponseSpecificationDecoratedFactory {

    ResponseSpecificationDecorated create(ResponseSpecificationImpl requestSpecification);
}
