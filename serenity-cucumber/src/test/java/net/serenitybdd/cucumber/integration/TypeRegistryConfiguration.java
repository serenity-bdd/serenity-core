package net.serenitybdd.cucumber.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import net.serenitybdd.cucumber.integration.steps.RpnCalculatorStepdefs;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;

//import io.cucumber.datatable.dependency.com.fasterxml.jackson.databind.ObjectMapper;

public class TypeRegistryConfiguration implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        Transformer transformer = new Transformer();
        typeRegistry.setDefaultDataTableCellTransformer(transformer);
        typeRegistry.setDefaultDataTableEntryTransformer(transformer);
        typeRegistry.setDefaultParameterTransformer(transformer);
        typeRegistry.defineDataTableType(new DataTableType(
                        RpnCalculatorStepdefs.Entry.class,
                        (Map<String, String> row) -> new RpnCalculatorStepdefs.Entry(
                                Integer.parseInt(row.get("first")),
                                Integer.parseInt(row.get("second")),
                                row.get("operation"))
                )
        );
    }

    private class Transformer implements ParameterByTypeTransformer, TableEntryByTypeTransformer, TableCellByTypeTransformer {
        ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Object transform(String s, Type type) {
            return objectMapper.convertValue(s, objectMapper.constructType(type));
        }

        /*@Override
        public <T> T transform(Map<String, String> map, Class<T> aClass, TableCellByTypeTransformer tableCellByTypeTransformer) {
            return objectMapper.convertValue(map, aClass);
        }

        @Override
        public <T> T transform(String s, Class<T> aClass) {
            return objectMapper.convertValue(s, aClass);
        }*/



        @Override
        public Object transform(Map<String, String> entryValue, Type toValueType, TableCellByTypeTransformer cellTransformer)
                throws Throwable {
            return null;
        }
    }

}
