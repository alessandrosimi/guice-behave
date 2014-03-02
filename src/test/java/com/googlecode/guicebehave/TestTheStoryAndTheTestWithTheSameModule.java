package com.googlecode.guicebehave;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TestRunner.class) @Modules(TestTheStory.Module.class)
public class TestTheStoryAndTheTestWithTheSameModule {

	@Test
	public void testIfTheModuleHasBeenAlreadyCreated() {
		assertEquals("Created once", 1, TestTheStory.Module.creationCounter);
	}

}
