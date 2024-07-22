package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
public class WebTestCockpitLinkFactory {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void links() {
    open(EngineCockpitUtil.testViewUrl("link-factory.xhtml"));
    $("h1").shouldBe(text("CockpitLinkFactory"));
    var links = $$(".ui-link");
    int pos = 0;
    while (pos < links.size()) {
      var link = links.get(pos++);
      var url = link.attr("href");
      link.click();
      webdriver().shouldHave(urlContaining(url));
      webdriver().shouldNotHave(title("Not Found"));
      webdriver().shouldHave(titles("Axon Ivy Engine", "Axon Ivy Cockpit"));
      Selenide.back();
      $("h1").shouldBe(text("CockpitLinkFactory"));
      links = $$(".ui-link");
    }
  }

  private ObjectCondition<WebDriver> titles(String... titles) {
    return new Titles(titles);
  }

  private static final class Titles implements ObjectCondition<WebDriver> {

    private List<String> expectedTitles;

    public Titles(String[] expectedTitles) {
      this.expectedTitles = List.of(expectedTitles);
    }

    @Override
    public String description() {
      return "should have titles " + expectedTitles;
    }

    @Override
    public String negativeDescription() {
      return "should not have titles " + expectedTitles;
    }

    @Override
    public CheckResult check(WebDriver driver) {
      String title = driver.getTitle();
      return result(driver, expectedTitles.contains(title), title);
    }

    @Override
    public String expectedValue() {
      return expectedTitles.toString();
    }

    @Override
    public String describe(WebDriver object) {
      return "Page";
    }
  }
}
