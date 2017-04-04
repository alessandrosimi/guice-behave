package io.exemplary.guice;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Scopes;
import io.exemplary.guice.TestTheTestRunnerWithInterceptorsInOrder.Counter;
import io.exemplary.guice.TestTheTestRunnerWithInterceptorsInOrder.Interceptor_IncrementByOne;
import io.exemplary.guice.TestTheTestRunnerWithInterceptorsInOrder.Interceptor_MutliplyByTwo;
import io.exemplary.guice.TestTheTestRunnerWithInterceptorsInOrder.Interceptor_SetToZero;

@RunWith(TestRunner.class)
@Modules(TestTheTestRunnerWithInterceptorsInOtherOrder.Module.class)
public class TestTheTestRunnerWithInterceptorsInOtherOrder {

  @Inject private Counter counter;
  
  @Test
  public void whenTheInterceptorsIncrementByOnSetToZeroeMutliplyByTwo_thenTheValueIsZero() {
    assertEquals(0, counter.value.get());
  }

  public static class Module extends AbstractTestModule {
    @Override
    protected void configureTest() {
      bind(Counter.class).in(Scopes.SINGLETON);
      bindTestInterceptor(Interceptor_IncrementByOne.class);
      bindTestInterceptor(Interceptor_SetToZero.class);
      bindTestInterceptor(Interceptor_MutliplyByTwo.class);
    }
  }

}
