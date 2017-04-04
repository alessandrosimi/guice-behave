package io.exemplary.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Guice;
import com.google.inject.Module;

/**
 * <p>Specifies the {@link Guice} modules
 * that define the context of the test.</p>
 * <pre>
 * {@literal @}RunWith(TestRunner.class) {@literal @}Modules(MyModules.class)
 * public class MyTest {
 *   ...
 * }</pre>
 * <p>This annotation is mandatory when {@link TestRunner}
 * or {@link StoryRunner} is used to run the test.</p>
 * @author alessandro.simi@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Modules {

	Class<? extends Module>[] value();
	
}

