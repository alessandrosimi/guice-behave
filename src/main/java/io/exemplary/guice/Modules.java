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

