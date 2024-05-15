package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.IntConsumer;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;

public class Conditions {

  public static WebElementCondition matchText(Pattern pattern) {
    return new MatchText(pattern);
  }

  public static WebElementCondition satisfiesText(IntConsumer consumer) {
    return new IntegerCondition(consumer);
  }

  public static final WebElementCondition INTEGER_TEXT = new IntegerCondition();
  public static final WebElementCondition NOT_NEGATIVE_INTEGER_TEXT = new IntegerCondition(i -> assertThat(i).isNotNegative());

  private static class IntegerCondition extends WebElementCondition {

    private IntConsumer consumer;

    public IntegerCondition() {
      this(i -> {});
    }

    public IntegerCondition(IntConsumer consumer) {
      super("Text is integer");
      this.consumer = consumer;
    }

    @Override
    public CheckResult check(Driver driver, WebElement element) {
      var text = element.getText();
      try {
        var i = Integer.parseInt(element.getText());
        consumer.accept(i);
        return new CheckResult(true, text);
      } catch(AssertionError | RuntimeException ex) {
        return new CheckResult(false, text);
      }
    }
  }

  private static class MatchText extends WebElementCondition {

    private Pattern pattern;

    public MatchText(Pattern pattern) {
      super("Text match pattern " +pattern);
      this.pattern = pattern;
    }

    @Override
    public CheckResult check(Driver driver, WebElement element) {
      var text = element.getText();
      if (pattern.matcher(text).matches()) {
        return new CheckResult(true, text);
      }
      return new CheckResult(false, text);
    }
  }
}
