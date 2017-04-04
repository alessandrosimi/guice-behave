/**
 * Copyright 2017 Alessandro Simi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.exemplary.guice;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Scopes;

@RunWith(TestRunner.class) @Modules(TestTheTestRunnerWithInterceptor.Module.class)
public class TestTheTestRunnerWithInterceptor {

	private static final String METHOD_NAME = "testTheInterceptor[A|B]";
	
	@Inject private Interceptor interceptor;
	@Inject private SingletonInterceptor singletonInterceptor;
	
	private boolean isFirst = true;
	
	@Test
	public void testTheInterceptorA() {
		checkIfTheInterceptorsHaveBeenInjected();
		checkIfTheSingletonState();
		checkTheOrder();
	}
	
	@Test
	public void testTheInterceptorB() {
		checkIfTheInterceptorsHaveBeenInjected();
		checkIfTheSingletonState();
		checkTheOrder();
	}
	
	private void checkIfTheInterceptorsHaveBeenInjected() {
		assertNotNull("The interceptor must be injected", interceptor);
		assertNotNull("The interceptor must be injected", singletonInterceptor);
	}
	
	private void checkIfTheSingletonState() {
		assertTrue("the interceptor is NOT a singleton", interceptor.isNotASingleton);
		assertTrue("the interceptor is a singleton", singletonInterceptor.isASingleton);
	}
	
	@SuppressWarnings("static-access")
	private void checkTheOrder() {
		if (isFirst) {
			assertTrue("beforeClassCreation() has run", interceptor.isClassCreated);
			assertTrue("beforeTestRuns() has run", interceptor.isBeforeTest);
			assertFalse("afterTestRuns() has run", interceptor.isAfterTest);
			isFirst = false;
		} else {
			assertTrue("beforeClassCreation() has run", Interceptor.isClassCreated);
			assertTrue("beforeTestRuns() has run", interceptor.isBeforeTest);
			assertTrue("afterTestRuns() has run", interceptor.isAfterTest);
		}
	}
	
	public static class Module extends AbstractTestModule {
		@Override
		protected void configureTest() {
			bindTestInterceptor(Interceptor.class);
			bind(SingletonInterceptor.class).in(Scopes.SINGLETON);
			bindTestInterceptor(SingletonInterceptor.class);
		}
	}
	
	public static class Interceptor extends TestInterceptor {
		private boolean isNotASingleton = true;
		private static boolean isClassCreated = false;
		private static boolean isBeforeTest = false;
		private static boolean isAfterTest = false;

		@Override
		public void beforeClassCreation(Class<?> classToRun) {
			assertEquals(TestTheTestRunnerWithInterceptor.class, classToRun);
			isClassCreated = true;
			isNotASingleton = false;
		}

		@Override
		public void beforeTestRuns(Method method) {
			assertTrue(method.getName().matches(METHOD_NAME));
			isBeforeTest = true;
		}

		@Override
		public void afterTestRuns(Method method) {
			assertTrue(method.getName().matches(METHOD_NAME));
			isAfterTest = false;
		}

	}
	
	public static class SingletonInterceptor extends TestInterceptor {
		
		private boolean isASingleton = false;

		@Override
		public void beforeClassCreation(Class<?> classToRun) {
			isASingleton = true;
			super.beforeClassCreation(classToRun);
		}
	
	}

}
