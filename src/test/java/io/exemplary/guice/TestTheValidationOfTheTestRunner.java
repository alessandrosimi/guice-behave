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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.google.inject.AbstractModule;

@RunWith(StoryRunner.class) @Modules(TestTheValidationOfTheTestRunner.Module.class)
public class TestTheValidationOfTheTestRunner {

	@Story
	public void testTheValidation() throws InitializationError {
		the_test_has_no_modules_annotation();
		the_test_has_empty_modules_annotation();
		the_test_has_wrong_modules_annotation();
	}
	
	@Expected(InitializationError.class)
	void the_test_has_no_modules_annotation() throws InitializationError {
		new TestRunner(WithoutModules.class);
	}
	
	public static class WithoutModules {
		@Test public void runnable() {}
	}
	
	@Expected(InitializationError.class)
	void the_test_has_empty_modules_annotation() throws InitializationError {
		new TestRunner(WithEmptyModules.class);
	}
	
	@Modules({})
	public static class WithEmptyModules {
		@Test public void runnable() {}
	}
	
	@Expected(InitializationError.class)
	void the_test_has_wrong_modules_annotation() throws InitializationError {
		new TestRunner(WithWrongModules.class);
	}
	
	@Modules(WrongModule.class)
	public static class WithWrongModules {
		@Test public void runnable() {}
	}
	
	public static class WrongModule extends AbstractModule {
		private WrongModule() {}
		@Override protected void configure() {}
	} 
	
	public static class Module extends AbstractModule {
		@Override protected void configure() {}
	}

}
