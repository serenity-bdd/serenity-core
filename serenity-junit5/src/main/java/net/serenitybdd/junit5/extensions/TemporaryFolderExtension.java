package net.serenitybdd.junit5.extensions;

import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;


public class TemporaryFolderExtension
  implements AfterEachCallback, TestInstancePostProcessor, ParameterResolver
{

  private final Collection<TemporaryFolder> tempFolders;
  
  public TemporaryFolderExtension() {
    tempFolders = new ArrayList<>();
  }


  private TemporaryFolder createTempFolder() {
    TemporaryFolder result = new TemporaryFolder();
    result.prepare();
    tempFolders.add( result );
    return result;
  }

  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    tempFolders.forEach( TemporaryFolder::cleanUp );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == TemporaryFolder.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createTempFolder();
  }

  @Override
  public void postProcessTestInstance(Object o, ExtensionContext extensionContext) throws Exception {
    Object testInstance = extensionContext.getTestInstance();
    stream( testInstance.getClass().getDeclaredFields() )
            .filter( field -> field.getType() == TemporaryFolder.class )
            .forEach( field -> injectTemporaryFolder( testInstance, field ) );
  }

  private void injectTemporaryFolder( Object instance, Field field ) {
    field.setAccessible( true );
    try {
      field.set( instance, createTempFolder() );
    } catch( IllegalAccessException iae ) {
      throw new RuntimeException( iae );
    }
  }
}
