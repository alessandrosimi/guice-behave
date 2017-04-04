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

import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

@RunWith(StoryRunner.class) @Modules({TestTheStoryException.Module.class, TestTheStoryException.OtherModule.class})
public class TestTheStoryException {

	@Inject private StoryNotConverter storyConverter;
	
	@Story
	public void testTheStoryWithExceptions() {
		check_if_the_converter_is_not_a_singleton();
		this_step_has_an_error();
		this_step_has_an_error_with_message();
		exceptionWrapper(new ExceptionRunner() {
			public void run() { this_step_has_an_expected_error(); }
		});
		exceptionWrapper(new ExceptionRunner() {
			public void run() { this_step_has_an_unexpected_error(); }
		});
		exceptionWrapper(new ExceptionRunner() {
			public void run() { this_step_has_an_different_error(); }
		});
		exceptionWrapper(new ExceptionRunner() {
			public void run() { this_step_has_an_unexpected_message(); }
		});
		exceptionWrapper(new ExceptionRunner() {
			public void run() { this_step_has_an_unexpected_null_message(); }
		});
	}
	
	void check_if_the_converter_is_not_a_singleton() {
		assertNotNull(storyConverter);
		assertNull(storyConverter.className);
	}
	
	@Expected(NullPointerException.class)
	void this_step_has_an_error() {
		throw new NullPointerException();
	}
	
	@Expected(value = NullPointerException.class, message = "\\d+")
	void this_step_has_an_error_with_message() {
		throw new NullPointerException("999");
	}
	
	void this_step_has_an_unexpected_error() {
		throw new NullPointerException();
	}

	@Expected(IllegalArgumentException.class)
	void this_step_has_an_expected_error() {}
	
	@Expected(IllegalArgumentException.class)
	void this_step_has_an_different_error() {
		throw new NullPointerException();
	}
	
	@Expected(value = NullPointerException.class, message = "[a-z]+")
	void this_step_has_an_unexpected_message() {
		throw new NullPointerException("999");
	}
	
	@Expected(value = NullPointerException.class, message = "[a-z]+")
	void this_step_has_an_unexpected_null_message() {
		throw new NullPointerException();
	}
	
	////////////
	// Module //
	////////////
	
	public static class Module extends AbstractStoryModule {
		@Override
		protected void configureStory() {
			bindStoryConverter(StoryNotConverter.class);
		}
	}
	
	public static class OtherModule extends AbstractModule {
		@Override protected void configure() {}
	}
	
	public static class StoryNotConverter implements AbstractStoryModule.StoryConverter {

		private String className;
		
		@Override
		public String convertClass(Class<?> clazz) {
			className = clazz.getSimpleName();
			return className;
		}

		@Override
		public String convertMethod(Method method, Object[] arguments) {
			return method.getName();
		}
		
	}
	
	private static interface ExceptionRunner {
		void run();
	}
	
	private void exceptionWrapper(ExceptionRunner runner) {
		boolean failed = false;
		try {
			runner.run();
		} catch (Exception e) {
			failed = true;
		}
		assertTrue("The execution must be failed", failed);
	}
	
}
