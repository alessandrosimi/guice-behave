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
