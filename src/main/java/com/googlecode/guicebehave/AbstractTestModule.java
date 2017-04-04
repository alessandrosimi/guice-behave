package com.googlecode.guicebehave;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * <p>Extend {@link AbstractTestModule} instead of {@link AbstractModule}
 * directly to be able to configure {@link TestInterceptor}.</p>
 * <pre>
 * public class MyTestModule extends AbstractTestModule {
 *   protected void configureTest() {
 *     bindTestInterceptor(MyTestInterceptor.class);
 *   }
 * }</pre>
 * <p>The test interceptor allows to run code before the test
 * creation and before and after the single test run.</p>
 * <pre>
 * public class MyTestInterceptor extends TestInterceptor {
 *   public void beforeClassCreation(final Class&lt;?&gt; classToRun) {
 *     [before class creation]
 *   }
 *   public void beforeTestRuns(final FrameworkMethod method) {
 *     [before test run]
 *   }
 *   public void afterTestRuns(final FrameworkMethod method) {
 *     [after test run]
 *   }
 * }</pre>
 * <p>The implementation can be bound in scope singleton with the
 * standard Guice configuration.</p>
 * @author alessandro.simi@gmail.com
 */
public abstract class AbstractTestModule extends AbstractModule {

	private TestInterceptor.Wrapper interceptors = new TestInterceptor.Wrapper();
	
	protected abstract void configureTest();
	
	@Override
	protected final void configure() {
		configureTest();
		bind(TestInterceptor.Wrapper.class).annotatedWith(Names.named(getClass().getName())).toInstance(interceptors);
	}
	
	/**
	 * Adds a test interceptor to the tests. The interceptors are executed in order.
	 * @param interceptor the class of the interceptor.
	 */
	protected final void bindTestInterceptor(Class<? extends TestInterceptor> interceptor) {
		interceptors.addInterceptor(interceptor);
	}

}
