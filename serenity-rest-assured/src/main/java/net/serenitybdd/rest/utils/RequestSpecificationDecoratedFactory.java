package net.serenitybdd.rest.utils;


import io.restassured.internal.RequestSpecificationImpl;
import net.serenitybdd.rest.decorators.request.RequestSpecificationDecorated;

public interface RequestSpecificationDecoratedFactory {

    RequestSpecificationDecorated create(RequestSpecificationImpl requestSpecification);
}
