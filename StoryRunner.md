# Introduction #

Guice-Bahave introduces the `Story` annotation to mark the test as behavioral test. The `Story` annotation behaves as the Junit annotation `Test` and tells JUnit to take in consideration the methods called within the test. The main method annotated with `Story` and all the other contribute to describe the intention and the behavior of the test.

The first way to do that is making the test readable converting the name of the methods in text.

```
@RunWith(StoryRunner.class) @Modules(MyModule.class)
public class MyStoryTest {
  @Story
  public void theTitleOfMyStory() {
    the_first_step_takes_$1_arguments("one");
    while_the_second_step_takes_the_list(1, 2, 3);
  }
   
  void the_first_step_takes_$1_arguments_$1(String numberOfArguments) {
    ...
  }
   
  void while_the_second_step_takes_the_list_$1(Integer ... numbers) {
   ...
  }
}
```

This test is printed in a log file in this way ..

```
[ story.TitleSpace]
[story.MyStoryTest]   Story "The title of my story"
[story.MyStoryTest]   by MyStoryTest
[ story.TitleSpace]
[story.MyStoryTest] The firsts step takes "one" arguments
[story.MyStoryTest] While the second step takes the list 1, 2 and 3
```
All the not private (public and package) test methods running during the test are translated and printed by default into the log.

Guice-Behave uses the generic [Slf4j](http://www.slf4j.org/) logger, therefore to log something it is necessary to add one of its implementations (see the [documentation](http://www.slf4j.org/manual.html) for more details or look the section Logging below).

# The translation #

The translation of the method use following simple rules:
  1. Camel case rule: add a space in front of each upper case character (not preceded by underscore `_`).
  1. Underscore rule: each underscore is replaced by a space. A double uppercase is replaced by a comma and a space.
  1. Arguments rule: the arguments of the method replace the $(number). For example the $1 refers to the first argument of the method. The story applies special translations for `String`, `Date` and `Collection` or `array`, otherwise uses calls the `toString()` method.

## Logging ##

This is an example of logging that is not included into the framework but can be used as implementation reference.

# Exception #

The method called by the test can represent an unsuccessful case to test, for example when the code throws an exception.

In order to test this case Guice Behave introduces the `@Exception` annotation.

```

@RunWith(StoryRunner.class) @Modules(MyModule.class)
public class MyStoryTest {
  @Story
  public void myStoryHasAProblem() {
    this_step_is_not_fine();
    also_this_one_is_not_great();
  }
   
  @Exception(NullPointerException.class)
  void this_step_is_not_fine(String numberOfArguments) {
    throw new NullPointerException("Problem");
  }
  
  @Exception(value = NullPointerException.class, message=".*eat")
  void also_this_one_is_not_great() {
   throw new NullPointerException("Great");
  }
}
```

The annotation matches the exception and also allows to define a regular expression to match the message.