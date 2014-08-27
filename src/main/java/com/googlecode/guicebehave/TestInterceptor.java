package com.googlecode.guicebehave;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.spi.InstanceBinding;

/**
 * <p>The {@link TestInterceptor} allows to run codes
 * before the test class has been created
 * ({@link #beforeClassCreation(Class)}), before the
 * test runs ({@link #beforeTestRuns(Method)}) and after
 * the test runs ({@link #afterTestRuns(Method)}).</p>
 * <pre>
 * public class MyInterceptor extends TestInterceptor {
 *   public void beforeClassCreation(Class<?> classToRun) {
 *     ...
 *   }
 *   public void beforeTestRuns(Method method) {
 *     ...
 *   }
 *   public void afterTestRuns(Method method) {
 *     ...
 *   }
 * }</pre>
 * <p>To allow the test to use the interceptor add a Guice
 * module that extends {@link AbstractTestModule} and call
 * the {@link #bindTestInterceptor(Class)} method with the
 * class of the interceptor created.</p>
 * <pre>
 * public class MyModule extends AbstractTestModule {
 *   public void configureTest() {
 *     bind(MyInterceptor.class).in(Scopes.SINGLETON); // Optional 
 *     bindTestInterceptor(MyInterceptor.class);
 *   }
 * }</pre>
 * <p>The interceptor is created by Guice so can access at
 * the same context of the test. If the interceptor has to
 * behave as a singleton is possible bind it into singleton
 * scope.</p>
 * @author alessandro.simi@gmail.com
 */
public abstract class TestInterceptor {
	
	public void beforeClassCreation(final Class<?> classToRun) {}
	
	public void beforeTestRuns(final Method method) {}
	
	public void afterTestRuns(final Method method) {}

	/**
	 * Containers of {@link TestInterceptor} classes.
	 * @author alessandro.simi@gmail.com
	 */
	static class Wrapper {
		
		private Set<Class<? extends TestInterceptor>> interceptorClasses = new LinkedHashSet<Class<? extends TestInterceptor>>();
		private Set<TestInterceptor> interceptors = new LinkedHashSet<TestInterceptor>();

		void addInterceptor(Class<? extends TestInterceptor> interceptor) {
			interceptorClasses.add(interceptor);
		}
		
		/**
		 * Adds the list of inteceptors classes from
		 * Wrapper instances bound with a named key
		 * equals to the name of the module.
		 */
		void addInterceptorsFromModules(Injector injector) {
			for (Entry<Key<?>, Binding<?>> entry : injector.getBindings().entrySet()) {
				Key<?> key = entry.getKey();
				Binding<?> binding = entry.getValue();
				if (binding instanceof InstanceBinding && isAWrapperBindingKey(key)) {
					Object instance = ((InstanceBinding<?>) binding).getInstance();
					if (instance instanceof Wrapper) {
						interceptorClasses.addAll(((Wrapper) instance).interceptorClasses);
					}
				}
			}
		}
		
		private boolean isAWrapperBindingKey(Key<?> key) {
			return key.getTypeLiteral().getRawType().equals(Wrapper.class);
		}

		Set<TestInterceptor> getInterceptors() {
			return interceptors;
		}

		/**
		 * Cleans and re-populate the interceptors set
		 * to delegate the singleton behavior of the
		 * interceptors to the Guice configuration.
		 */
		void setInterceptors(Injector injector) {
			interceptors.clear();
			for (Class<? extends TestInterceptor> interceptorClass : interceptorClasses) {
				TestInterceptor interceptor = injector.getInstance(interceptorClass);
				this.interceptors.add(interceptor);
			}
		}
		
	}

}
