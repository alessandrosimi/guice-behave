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

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

@RunWith(TestRunner.class) @Modules(TestTheTestRunner.Module.class)
public class TestTheTestRunner {

	@Inject @Named(NAME) private String name;
	
	@Test
	public void testTheInjection() {
		assertNotNull("The string must be injected", name);
		assertEquals(NAME_INSTANCE, name);
	}

	private static final String NAME = "name";
	private static final String NAME_INSTANCE = "Bob";
	
	public static class Module extends AbstractModule {
		@Override
		protected void configure() {
			bind(String.class).annotatedWith(Names.named(NAME)).toInstance(NAME_INSTANCE);
		}
	}

}
