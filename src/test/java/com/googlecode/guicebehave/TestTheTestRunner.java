package com.googlecode.guicebehave;

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
