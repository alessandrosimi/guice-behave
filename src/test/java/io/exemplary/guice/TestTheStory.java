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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Scopes;

@RunWith(StoryRunner.class) @Modules(TestTheStory.Module.class)
public class TestTheStory {

	private final static String className = TestTheStory.class.getSimpleName();
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Inject private StoryPrinterToProperty storyPrinter;
	
	@Story
	public void testTheStoryPrinter() {
		assertStoryEquals("Story \"Test the story printer\"");
		this_is_a_step_with_the_number_$1_as_argument(2);
		this_step_checks_the_comma__and_the_string_parameter_$1("String");
		this_step_has_a_date_$1_as_parameter_an_$2_value(new Date(), null);
		this_step_has_a_list_of_$1_and_an_array_of_$2(Arrays.asList(1L, 2L), new String[]{"one", "two", "three"});
		this_step_has_out_of_bound_arguments_$1();
		this_step_cannot_be_print_$1(new NotPrintableArgument());
		this_step_should_print_an_object_as_$1(new PrintableBean("name", 1));
		this_step_should_print_an_object_as_$1_with_missing_value(new PrintableBean("name", 3));
	}

	private void assertStoryEquals(String story) {
		assertNotNull(storyPrinter);
		assertEquals(storyPrinter.className, className);
		assertEquals(story, storyPrinter.story);
	}
	
	void this_is_a_step_with_the_number_$1_as_argument(int numberOfStory) {
		assertStepEquals("This is a step with the number " + numberOfStory + " as argument");
	}
	
	private void assertStepEquals(String step) {
		assertNotNull(storyPrinter);
		assertEquals(storyPrinter.className, className);
		assertEquals(step, storyPrinter.stepName);
	}
	
	void this_step_checks_the_comma__and_the_string_parameter_$1(String string) {
		assertStepEquals("This step checks the comma, and the string parameter \"" + string + "\"");
	}
	
	void this_step_has_a_date_$1_as_parameter_an_$2_value(@Theory Date date, Object object) {
		assertStepEquals("This step has a date " + dateFormat.format(date) + " as parameter an <empty> value");
	}
	
	void this_step_has_a_list_of_$1_and_an_array_of_$2(List<Long> longs, String[] strings) {
		assertStepEquals("This step has a list of " + longs.get(0) + " and " + longs.get(1)  + " and an array of \"" + strings[0] + "\", \"" + strings[1] + "\" and \"" + strings[2] + "\"");
	}
	
	void this_step_has_out_of_bound_arguments_$1() {
		assertStepEquals("This step has out of bound arguments <out_of_bound_argument>");
	}
	
	void this_step_cannot_be_print_$1(NotPrintableArgument notPrintableArgument) {
		assertStepEquals("this step cannot be print  $1");
	}
	
	void this_step_should_print_an_object_as_$1(@Tell("a bean that contains ${name} and ${value}") PrintableBean bean) {
		assertStepEquals("This step should print an object as a bean that contains \"name\" and 1");
	}
	
	void this_step_should_print_an_object_as_$1_with_missing_value(@Tell("a bean that contains ${name} and ${values}") PrintableBean bean) {
		assertStepEquals("This step should print an object as a bean that contains \"name\" and " + MethodConverter.ReplaceArguments.FIELD_NOT_FOUND + " with missing value");
	}
	
	@Story
	public void testIfTheModuleHasBeenAlreadyCreated() {
		assertEquals("Created once", 1, TestTheStory.Module.creationCounter);
	}
	
	@Story(id = "ID")
	public void testTheStoryId() {
		assertStoryEquals("Story \"Test the story id\" [ID]");
	}
	
	@Story @Ignore
	public void testAnIgnoredStory() {
		fail("This test shouldn't be executed");
	}
	
	@Story
	public void testTheExlusionOfTheFinalizeMethod() throws Throwable {
		assertStoryEquals("Story \"Test the exlusion of the finalize method\"");
		finalize();
		assertStoryEquals("Story \"Test the exlusion of the finalize method\"");
	}
	
	////////////
	// Module //
	////////////
	
	public static class Module extends AbstractStoryModule {
		
		static int creationCounter;
		
		@Override
		protected void configureStory() {
			bind(StoryPrinterToProperty.class).in(Scopes.SINGLETON);
			bindStoryPrinter(StoryPrinterToProperty.class);
			creationCounter++;
		}
	}
	
	public static class StoryPrinterToProperty implements AbstractStoryModule.StoryPrinter {

		private String className;
		private String story;
		private String stepName;
		
		@Override
		public void onStoryBegins(String className, String story) {
			this.className = className;
			this.story = story;
		}

		@Override
		public void onStepBegins(String className, String stepName) {
			this.className = className;
			this.stepName = stepName;
		}

		@Override
		public void onStepEnds(String className, String stepName) {
			this.className = className;
			this.stepName = stepName;
		}

		@Override
		public void onStoryEnds(String className, String story) {
			this.className = className;
			this.story = story;
		}
		
	}
	
	private class NotPrintableArgument {
		@Override
		public String toString() { throw new NullPointerException(); }
	}	
	
	private class PrintableBean {
		private final String name;
		private final int value;
		
		public PrintableBean(String name, int value) {
			this.name = name;
			this.value = value;
		}

		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		@SuppressWarnings("unused")
		public int getValue() {
			return value;
		};
		
	}

}
