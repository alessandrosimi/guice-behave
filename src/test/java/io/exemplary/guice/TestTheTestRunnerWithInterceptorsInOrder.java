package io.exemplary.guice;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Scopes;

@RunWith(TestRunner.class)
@Modules(TestTheTestRunnerWithInterceptorsInOrder.Module.class)
public class TestTheTestRunnerWithInterceptorsInOrder {

  @Inject private Counter counter;
  
  @Test
  public void whenTheInterceptorsSetToZeroIncrementByOneMutliplyByTwo_thenTheValueIsTwo() {
    assertEquals(2, counter.value.get());
  }

  public static class Module extends AbstractTestModule {
    @Override
    protected void configureTest() {
      bind(Counter.class).in(Scopes.SINGLETON);
      bindTestInterceptor(Interceptor_SetToZero.class);
      bindTestInterceptor(Interceptor_IncrementByOne.class);
      bindTestInterceptor(Interceptor_MutliplyByTwo.class);
    }
  }

  public static class Counter {
    final AtomicInteger value = new AtomicInteger(0);
  }

  public static class Interceptor_SetToZero extends TestInterceptor {

    @Inject private Counter counter;
    
    @Override
    public void beforeClassCreation(Class<?> classToRun) {
      counter.value.set(0);
    }

  }

  public static class Interceptor_IncrementByOne extends TestInterceptor {

    @Inject private Counter counter;
    
    @Override
    public void beforeClassCreation(Class<?> classToRun) {
      counter.value.incrementAndGet();
    }

  }

  public static class Interceptor_MutliplyByTwo extends TestInterceptor {

    @Inject private Counter counter;
    
    @Override
    public void beforeClassCreation(Class<?> classToRun) {
      int value = counter.value.get();
      counter.value.set(value * 2);
    }

  }

}
