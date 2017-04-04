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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.google.inject.Module;

/** 
 * <p>The test can be logically split into steps where each
 * step is a part of a story. The {@link StoryRunner} listens
 * the execution of each part of the test and try to translate
 * it in readable story.</p>
 * <pre>
 * {@literal @}RunWith(StoryRunner.class) {@literal @}Modules(MyModule.class)
 * public class MyStoryTest {
 *   {@literal @}Story
 *   public void theTitleOfMyStory() {
 *     the_first_step_takes_$1_arguments("one");
 *     while_the_second_step_takes_the_list(1, 2, 3);
 *   }
 *   
 *   void the_first_step_takes_$1_arguments_$1(String numberOfArguments) {
 *     ...
 *   }
 *   
 *   void while_the_second_step_takes_the_list_$1(Integer ... numbers) {
 *     ...
 *   }
 * }</pre>
 * <p>This test is translated into the following text...</p>
 * <pre>
 * - The title of my story
 * The first step takes "one" arguments
 * While the second step takes the list 1, 2 and 3</pre>
 * <p>The story is printed by default with the generic
 * <a href="http://www.slf4j.org/">Slf4j</a> logger under
 * the {@code story} package so it is possible to filter
 * the log in a separate file.</p>
 * <p>The way the methods are translated or where the translation
 * is printed can be configured with the {@link AbstractStoryModule.StoryConverter}
 * and {@link AbstractStoryModule.StoryPrinter} interfaces and the
 * {@link AbstractStoryModule}.</p>
 */  
public final class StoryRunner extends TestRunner {
	
	private static Map<String, StoryInterceptor.Module> storyModules = new ConcurrentHashMap<String, StoryInterceptor.Module>();
	
	/** 
	 * Creates a Runner with Guice modules. 
	 * @param classToRun the test class to run 
	 * @throws InitializationError if the test class is malformed 
	 */  
	public StoryRunner(final Class<?> classToRun) throws InitializationError {  
		super(classToRun);
		Modules annotation = getModulesAnnotation(classToRun);
		getStoryModule(annotation).clearInstances();
	}

	@Override
	List<Module> getModules(Modules annotation) throws InitializationError {
		List<Module> modules = super.getModules(annotation);
		modules.add(getStoryModule(annotation));
		return modules;
	}
	
	private StoryInterceptor.Module getStoryModule(Modules annotation) throws InitializationError {
		String key = getKey(annotation);
		if (!storyModules.containsKey(key)) {
			storyModules.put(key, new StoryInterceptor.Module());
		}
		return storyModules.get(key);
	}

	@Override
	protected final List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(Story.class);
	}

	@Override
	protected final void validateTestMethods(List<Throwable> errors) {
		validatePublicVoidNoArgMethods(Story.class, false, errors);
	}
	
}