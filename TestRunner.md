# Introduction #
The `TestRunner` runs the test into the Google Guice context defined by the modules into the `Modules` annotation.
```
@RunWith(TestRunner.class) @Modules(MyModule.class)
public class MyTest {
  @Inject private MyClass myClass;

  @Test
  public void testWithGuice() {
    assertNotNull("Guice works!", myClass);
  }
}
```
The `Modules` annotation can define on or more modules.

# Test-Driven Development #

It is easy to follow a TDD process within a IoC framework (as Guice is) because it minimizes the inter-dependency of each class.

This is particularly useful during the test of a single unit (method or class) because it is possible to mock everything that is not under test.

In this scenario Google Guice helps to mock the dependencies inside the configuration `Module` where it is possible to define alternative (or mock) implementation of the test dependencies.

The other two vantages are the re-usability of mocks because are defined into the `Module`s and not into the test and the choice to use a real implementation instead of the mocks.

# Test Interceptor #

It is possible to define generic test interceptors that execute code before the test creation or before and after the test.
```
public class MyInterceptor extends TestInterceptor {
  public void beforeClassCreation(Class<?> classToRun) {
    ...
  }
  public void beforeTestRuns(Method method) {
    ...
  }
  public void afterTestRuns(Method method) {
    ...
  }
}
```
The interceptor can be add to the test creating a Google Guice module that extends `AbstractTestModule`.
```
public class MyModule extends AbstractTestModule {
  public void configureTest() {
    bindTestInterceptor(MyInterceptor.class);
  }
}
```
The interceptor is create by Guice so it is possible to inject dependencies into and also it is possible to bind it in a singleton scope.