# Introduction
Guice-Behave is a test framework for [Google Guice](https://code.google.com/p/google-guice/) applications, created to help writing tests that follow [TDD](http://en.wikipedia.org/wiki/Test-driven_development) and [BDD](http://en.wikipedia.org/wiki/Behavior-driven_development) methodologies. The main idea of the framework is using the language to auto-describe the test avoiding to use other tools or sources.
## How it works
Create a [Junit 4](http://junit.org/) test and add `RunWith` annotation with `TestRunner` class and the `Modules` annotation.
```java
@RunWith(TestRunner.class) @Modules(MyModule.class)
public class MyTest {
  @Inject private MyClass myClass;

  @Test
  public void testWithGuice() {
    assertNotNull("Guice should works!", myClass);
  }
}
```
The test runs in the Guice context defined into `MyModule` class.
```java
public MyModule extends AbstractModule {
  protected void configure() {
    bind(MyClass.class).to(MyClassImplementation);
  }
}
```
For more details refer to the TestRunner page.
## Tell a story
Guice-Behave helps your tests to tell a story. Each part of the story comes from the names methods running during the tests. For example this test...
```java
@RunWith(StoryRunner.class) @Modules(MyModule.class)
public void NelsonMandelaTest {
  @Story
  public void FamousSpeech{
    i_have_cherished_the_ideal_of_a_$1_society("democratic", "free");
    in_which_all_persons_live_together_in_harmony();
    and_with_equal_opportunities().
  }

  void i_have_cherished_the_ideal_of_a_$1_society(String typesOfSociety) { ... }

  void in_which_all_persons_live_together_in_harmony() { ... }

  void and_with_equal_opportunities() { ... }

}
```
... is translated in this [ref](http://www.anc.org.za/show.php?id=3430) ...
```
  Story "Famous speech"
  by NelsonMandelaTest
I have cherished the ideal of a democratic and free society
In which all persons live together in harmony
And with equal opportunities
```
This translation helps to write and organize high-level behavioral tests with no effort, using what the language offers and encouraging the re-usability of the tests. For more details about refer to the StoryRunner page.
