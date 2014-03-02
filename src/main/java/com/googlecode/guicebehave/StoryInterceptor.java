package com.googlecode.guicebehave;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.guicebehave.AbstractStoryModule.StoryConverter;
import com.googlecode.guicebehave.AbstractStoryModule.StoryPrinter;

/**
 * <p>Intercept methods annotated with {@link Story} and
 * generating log for every method with the message extrapolated
 * from the method name.</p>
 * <p>It also implements the logic for expected Exceptions
 * when the annotation {@link Expected} is present with the
 * same logic of {@link Test} annotation.</p>
 * @author alessandro.simi
 */
class StoryInterceptor implements MethodInterceptor {

	@Inject private Injector injector;
	
	private StoryPrinter printer;
	
	private StoryPrinter printer() {
		if (printer == null) {
			try {
				printer = injector.getInstance(StoryPrinter.class);
			} catch (ConfigurationException e) {
				printer = injector.getInstance(StoryPrinter.Default.class);
			}
		}
		return printer;
	}
	
	private StoryConverter converter;
	
	private StoryConverter converter() {
		if (converter == null) {
			try {
				converter = injector.getInstance(StoryConverter.class);
			} catch (ConfigurationException e) {
				converter = injector.getInstance(StoryConverter.Default.class);
			}
		}
		return converter;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!isFinalizeMethod(invocation)) {
			Story story = invocation.getMethod().getAnnotation(Story.class);
			if (story != null) {
				return invokeStory(invocation);
			} else {
				return invokeStep(invocation);
			}
		} else {
			return invocation.proceed();
		}
	}

	private final static String FINALIZE = "finalize";

	private boolean isFinalizeMethod(MethodInvocation invocation) {
		return FINALIZE.equalsIgnoreCase(invocation.getMethod().getName());
	}
	
	/**
	 * Invocation of the main story method.
	 */
	private Object invokeStory(MethodInvocation invocation) throws Throwable {
		printer().onStoryBegins(classToString(invocation), methodToString(invocation));
		Object object = invocation.proceed();
		printer().onStoryEnds(classToString(invocation), methodToString(invocation));
		return object;
	}
	
	/**
	 * Invocation of steps methods.
	 */
	private Object invokeStep(MethodInvocation invocation) throws Throwable {
		printer().onStepBegins(classToString(invocation), methodToString(invocation));
		Expected expected = invocation.getMethod().getAnnotation(Expected.class);
		Object result = null;
		if (expected == null) {
			result = invocation.proceed();
		} else {
			boolean succeed = false;
			try {
				result = invocation.proceed();
				succeed = true;
			} catch (Throwable throwable) {
				verifyException(expected, throwable);
			}
			if (succeed) {
				throw new Exception("Expected exception " + expected.value().getName());
			}
		}
		printer().onStepEnds(classToString(invocation), methodToString(invocation));
		return result;
	}
	
	private void verifyException(Expected expected, Throwable actual) throws Exception {
		String expectedMessage = expected.message();
		String actualMessage = actual.getMessage();
		if (!expected.value().isAssignableFrom(actual.getClass()) ) {
            throw new Exception("Unexpected exception, expected<" + expected.value().getName() + "> but was <" + actual.getClass().getName() + ">", actual);
		} else if (!expectedMessage.isEmpty() && (actualMessage == null || !actualMessage.matches(expectedMessage))) {
            throw new Exception("Unexpected message exception, expected match with <" + expected.message() + "> but was <" + actual.getMessage() + ">", actual);
		}
	}
	
	private String classToString(MethodInvocation invocation) {
		return converter().convertClass(invocation.getMethod().getDeclaringClass());
	}
	
	private String methodToString(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		return converter().convertMethod(method, arguments);
	}
	
	/**
	 * {@link com.google.inject.Guice} module to bind the {@link StoryInterceptor}.
	 * @author alessandro.simi@gmail.com
	 */
	public static class Module extends AbstractStoryModule {
		
		private StoryInterceptor interceptor;
		
		@Override
		protected void configureStory() {
			interceptor = new StoryInterceptor();
			requestInjection(interceptor);
			bindInterceptor(annotatedWith(RunWith.class), any(), interceptor);
		}
		
		/**
		 * Clears the printer and converter instances
		 * every test run to delegate the singleton
		 * behave to Guice.
		 */
		void clearInstances() {
			interceptor.printer = null;
			interceptor.converter = null;
		}
		
	}

}
