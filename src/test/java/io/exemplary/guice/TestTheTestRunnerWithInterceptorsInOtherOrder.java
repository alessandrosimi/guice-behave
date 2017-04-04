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
