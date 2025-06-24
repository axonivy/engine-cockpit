package ch.ivyteam.enginecockpit.util;

import java.util.Objects;

import org.openqa.selenium.WebDriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.Selenide;

public class WebDriverConditions {

  public static ObjectCondition<WebDriver> jsReturnsValue(String expression, String expectedValue) {
    return new JsReturnsValueCondition(expression, expectedValue);
  }


  public static class JsReturnsValueCondition implements ObjectCondition<WebDriver> {
    private String expression;
    private String expectedValue;

    protected JsReturnsValueCondition(String expression, String expectedValue) {
      this.expression = expression;
      this.expectedValue = expectedValue;
    }

    @Override
    public String expectedValue() {
      return expectedValue.toString();
    }

    @Override
    public String description() {
      return expression + " should have value " + expectedValue;
    }

    @Override
    public String negativeDescription() {
      return expression + " should not have value " + expectedValue;
    }

    @Override
    public String describe(WebDriver webDriver) {
      return "webdriver";
    }

    @Override
    public CheckResult check(WebDriver webDriver) {
      var result = Selenide.executeJavaScript("return " + expression);
      return result(webDriver, Objects.equals(result.toString(), expectedValue), result);
    }
  }

}
