package io.exemplary.guice;

import java.lang.reflect.Method;

import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * <p>This is a {@link Guice} module where is possible
 * configure the {@link TestInterceptor} as described
 * in {@link AbstractTestModule} class.</p>
 * <p>It is also possible to override the {@link StoryPrinter}
 * and {@link StoryConverter} default implementations.</p>
 * <pre>
 * public class MyStoryModule extends AbstractStoryModule {
 *   protected void configureStory() {
 *     bindStoryPrinter(MyStoryPrinter.class);
 *     bindStoryConverter(MyStoryConverter.class);
 *   }
 * }</pre>
 * <p>The {@link StoryPrinter} interface specifies how print
 * story events and {@link StoryConverter} interface converts
 * the events in messages printable by the printer.</p>
 * @author alessandro.simi@gmail.com
 */
public abstract class AbstractStoryModule extends AbstractTestModule {

	protected abstract void configureStory();
	
	@Override
	protected final void configureTest() {
		configureStory();
	}
	
	/**
	 * Binds the implementation of {@link StoryPrinter}
	 * to the interface so it can be used by the test.
	 * @param storyPrinter class of the implementation.
	 */
	protected final void bindStoryPrinter(Class<? extends StoryPrinter> storyPrinter) {
		bind(StoryPrinter.class).to(storyPrinter);
	}
	
	/**
	 * Contains the four events triggered during a test.
	 * @author alessandro.simi@gmail.com
	 */
	public static interface StoryPrinter {
		
		void onStoryBegins(String className, String story);
		
		void onStepBegins(String className, String stepName);

		void onStepEnds(String className, String stepName);
		
		void onStoryEnds(String className, String story);
	
		public static class Default implements StoryPrinter {

			private final static String PREFIX = "story.";
			private static final String SPACE = "  ";
			private final static String TITLE = "TitleSpace";
			private static final String BY = "by ";	

			@Override
			public void onStoryBegins(String className, String story) {
				log(TITLE, "");
				log(className, SPACE + story);
				log(className, SPACE + BY + className);
				log(TITLE, "");
			}

			@Override
			public void onStepBegins(String className, String step) {
				log(className, step);
			}

			@Override
			public void onStepEnds(String className, String step) {}

			@Override
			public void onStoryEnds(String className, String story) {}
			
			private void log(String className, String message) {
				LoggerFactory.getLogger(PREFIX + className).info(message);
			} 
			
		}
		
	}

	/**
	 * Binds the implementation of {@link StoryConverter}
	 * to the interface so it can be used by the test.
	 * @param storyConverter class of the implementation.
	 */
	protected final void bindStoryConverter(Class<? extends StoryConverter> storyConverter) {
		bind(StoryConverter.class).to(storyConverter);
	}
	
	/**
	 * Contains the methods to convert the test class
	 * and test methods to {@link String}.
	 * @author alessandro.simi@gmail.com
	 */
	public static interface StoryConverter {
		
		String convertClass(Class<?> clazz);
		
		String convertMethod(Method method, Object[] arguments);
		
		public static class Default implements StoryConverter {

			@Inject private MethodConverter.Global toMessage;
			
			@Override
			public String convertClass(Class<?> clazz) {
				return clazz.getSimpleName();
			}

			@Override
			public String convertMethod(Method method, Object[] arguments) {
				String message = toMessage(method, arguments);
				Story story = method.getAnnotation(Story.class);
				if (story != null) {
					message = "Story \"" + message + "\"";
					if (!story.id().isEmpty()) {
						message += " [" + story.id() + "]";
					}
				}
				return message;
			}
			
			protected final String toMessage(Method method, Object[] arguments) {
				return toMessage.convert(method.getName(), method, arguments);
			}
			
		}
		
	}

}
