package com.googlecode.guicebehave;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;

@RunWith(StoryRunner.class) @Modules(TestTheStory.Module.class)
public class TestTheStoryWithTheSameModule {
	
	@Story
	public void testIfTheModuleHasBeenAlreadyCreated() {
		assertEquals("Created once", 1, TestTheStory.Module.creationCounter);
	}
	
}
