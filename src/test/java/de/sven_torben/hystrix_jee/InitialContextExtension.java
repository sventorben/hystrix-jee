package de.sven_torben.hystrix_jee;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.naming.Context;

class InitialContextExtension implements
        AfterEachCallback, BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

  private String factory;

  @Override
  public void afterAll(ContainerExtensionContext containerExtensionContext) throws Exception {
    if (factory == null) {
      System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
    } else {
      System.setProperty(Context.INITIAL_CONTEXT_FACTORY, factory);
      factory = null;
    }
  }

  @Override
  public void beforeAll(ContainerExtensionContext containerExtensionContext) throws Exception {
    factory = System.getProperty(Context.INITIAL_CONTEXT_FACTORY);
    System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        MockInitialContextFactory.class.getName());
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext)
          throws Exception {
    Context initialContextMock = mock(Context.class);
    MockInitialContextFactory.setCurrentContext(initialContextMock);
    Optional<Field> injectionPoint = determineInjectionPoint(testInstance.getClass());
    injectionPoint.ifPresent(
        field -> injectContext(field, testInstance, initialContextMock)
    );
  }

  private void injectContext(Field field, Object testInstance, Context context) {
    field.setAccessible(true);
    try {
      field.set(testInstance, context);
    } catch (IllegalAccessException iae) {
      throw new RuntimeException(iae);
    }
  }

  private static Optional<Field> determineInjectionPoint(Class<?> type)
      throws NoSuchFieldException {
    List<Field> fields = determineFields(type);
    return fields.stream()
        .filter(field -> Context.class.isAssignableFrom(field.getType()))
        .findFirst();
  }

  private static List<Field> determineFields(Class<?> testClass) {
    List<Field> fields = new ArrayList<>();
    fields.addAll(Arrays.asList(testClass.getDeclaredFields()));
    fields.addAll(Arrays.asList(testClass.getFields()));
    return fields;
  }

  @Override
  public void afterEach(TestExtensionContext context) throws Exception {
    MockInitialContextFactory.clearCurrentContext();
  }
}
