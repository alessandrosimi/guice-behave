package com.googlecode.guicebehave;  

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Ignore;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.googlecode.guicebehave.TestInterceptor.Wrapper;

/** 
 * <p>This JUnit test runner allow to run the test
 * into a Guice context. The modules defining the
 * context are defined with the {@link Modules}
 * annotation.</p><pre>
 * {@literal @}RunWith(TestRunner.class) {@literal @}Modules(MyModule.class)
 * public class MyTest {
 * 
 *   {@literal @}Inject private MyClass myClass;
 * 
 *   {@literal @}Test
 *   public void testWithGuice() {
 *     assertNotNull("Guice works!", myClass);
 *   }
 * }</pre>
 * <p>The Guice module(s) defined into the {@link Modules}
 * annotation can extend the {@link AbstractTestModule}
 * abstract class where is possible to configure the
 * test interceptors {@link TestInterceptor}.</p><pre>
 * public class MyModule extends AbstractTestModule {
 *   protected void configureTest() {
 *     bindTestInterceptor(MyTestInterceptor.class);
 *   }
 * }
 * </pre>
 * <p>An interceptor allows to execute code before and
 * after the single test runs and before the class is
 * created.</p>
 */  
public class TestRunner extends BlockJUnit4ClassRunner {

	private final static Map<String, Injector> injectors = new ConcurrentHashMap<String, Injector>();
	private final static Map<String, TestInterceptor.Wrapper> interceptors = new ConcurrentHashMap<String, TestInterceptor.Wrapper>();
	
	private final Injector injector;
	private final TestInterceptor.Wrapper testInterceptors;
	
	/** 
	 * Creates a Runner with Guice modules. 
	 * @param classToRun the test class to run 
	 * @throws InitializationError if the test class is malformed 
	 */  
	public TestRunner(final Class<?> classToRun) throws InitializationError {  
		super(classToRun);
		injector = getInjector(classToRun);
		testInterceptors = getInterceptors(classToRun);
		for (TestInterceptor interceptor : testInterceptors.getInterceptors()) {
			interceptor.beforeClassCreation(classToRun);
		}
	}
	
	/**
	 * @return the proper {@link Injector} based on the modules
	 * inside the {@link Modules} annotation.
	 */
	final Injector getInjector(Class<?> classToRun) throws InitializationError {
		Modules annotation = getModulesAnnotation(classToRun);
		String key = getKey(annotation);
		Injector injector = injectors.get(key);
		if (injector == null) {
			injector = Guice.createInjector(getModules(annotation));
			injectors.put(key, injector);
		}
		return injector;
	}
	
	final Modules getModulesAnnotation(Class<?> classToRun) throws InitializationError {
		Modules annotation = classToRun.getAnnotation(Modules.class);
		if (annotation != null) {
			return annotation;
		} else {
			throw new InitializationError("Impossible to find @Modules annotation. Did you forget to add it with @RunWith annotation.");
		}
	}
	
	final String getKey(Modules annotation) throws InitializationError {
		Class<? extends Module>[] classes = annotation.value();
		if (classes.length == 0) {
			throw new InitializationError("The @Modules annotation doesn't list any Guice modules.");
		}
		return Arrays.toString(getModuleClasses(annotation));
	}
	
	/**
	 * @return an array of {@link Module}s class from the
	 * {@link Modules} annotation ordered by name.
	 */
	@SuppressWarnings("unchecked")
	final Class<? extends Module>[] getModuleClasses(Modules annotation) {
		Set<Class<? extends Module>> set = new LinkedHashSet<Class<? extends Module>>();
		for (Class<? extends Module> clazz : annotation.value()) {
			set.add(clazz);
		}
		Class<? extends Module>[] classes = set.toArray(new Class[set.size()]);
		Arrays.sort(classes, new Comparator<Class<? extends Module>>() {
			@Override
			public int compare(Class<? extends Module> o1, Class<? extends Module> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return classes;
	}
	
	/**
	 * @return a list of {@link Module}s instances from
	 * the {@link Modules} annotation. 
	 */
	List<Module> getModules(Modules annotation) throws InitializationError {
		Class<? extends Module>[] moduleClasses = getModuleClasses(annotation);
		List<Module> modules = new ArrayList<Module>(moduleClasses.length);
		for (int i = 0; i < moduleClasses.length; i++) {
			Class<? extends Module> moduleClass = moduleClasses[i];
			try {
				modules.add(moduleClasses[i].newInstance());
			} catch (Exception e) {
				throw new InitializationError("Impossible to instantiate the " + moduleClass.getName() + " Guice Module.");
			}
		}
		return modules;
	}
	
	/**
	 * @return the proper {@link Wrapper} based on the modules
	 * inside the {@link Modules} annotation.
	 */
	final TestInterceptor.Wrapper getInterceptors(Class<?> classToRun) throws InitializationError {
		Modules annotation = getModulesAnnotation(classToRun);
		String key = getKey(annotation);
		TestInterceptor.Wrapper testInterceptors = interceptors.get(key);
		if (testInterceptors == null) {
			testInterceptors = new TestInterceptor.Wrapper();
			testInterceptors.addInterceptorsFromModules(injector);
			interceptors.put(key, testInterceptors);
		}
		testInterceptors.setInterceptors(injector);
		return testInterceptors;
	}
	
	////////////////////////
	// Overridden Methods //
	////////////////////////

	/*
	 * Create class instance instance
	 * using guice.
	 */
	@Override  
	public final Object createTest() {  
		return injector.getInstance(getTestClass().getJavaClass());  
	}
	
	/*
	 * Guice can inject constructors with parameters
	 * so we don't want this method to trigger an error 
	 */
	@Override  
	protected final void validateZeroArgConstructor(List<Throwable> errors) {}  
	
	////////////////
	// Run Method //
	////////////////
	
	/**
	 * <p>This method runs the test. It was overridden in order
	 * to add the test interceptor.</p>
	 * <p>This method check two annotation before invoke the test
	 * method.</p>
	 * <ol>
	 * <li>Checks {@link Ignore} annotation. If exists the test is
	 * ignored.</li>
	 * <li>Invoke beforeTestRun method of the test interceptor.</li>
	 * <li>Invoke the test method: run the test.</li>
	 * <li>Invoke afterTestRun method of the test interceptor.</li>
	 * </ol>
	 */
	@Override
	protected final void runChild(FrameworkMethod method, RunNotifier notifier) {
		EachTestNotifier eachNotifier = makeNotifier(method, notifier);
		// Ignore Test
		if (method.getAnnotation(Ignore.class) != null) {
			eachNotifier.fireTestIgnored();
			return;
		}
		// Before test
		for (TestInterceptor interceptor : testInterceptors.getInterceptors()) {
			interceptor.beforeTestRuns(method.getMethod());
		}
		// Start
		eachNotifier.fireTestStarted();
		try {
			// Run
			methodBlock(method).evaluate();
		} catch(AssumptionViolatedException e) {
			// Assumption Error
			eachNotifier.addFailedAssumption(e);
		} catch(Throwable e) {
			// Error
			eachNotifier.addFailure(e);
		} finally {
			// Finish
			eachNotifier.fireTestFinished();
		}
		// After test
		for (TestInterceptor interceptor : testInterceptors.getInterceptors()) {
			interceptor.afterTestRuns(method.getMethod());
		}
	}
	
	private EachTestNotifier makeNotifier(FrameworkMethod method, RunNotifier notifier) {
		Description description = describeChild(method);
		return new EachTestNotifier(notifier, description);
	}
	
}