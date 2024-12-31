package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.openqa.selenium.WebDriver;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
public class WebTestCockpitLinkFactory {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @TestFactory
  Collection<DynamicTest> link() {
    open(EngineCockpitUtil.testViewUrl("link-factory.xhtml"));
    $("h1").shouldBe(text("CockpitLinkFactory"));
    var links = $$(".ui-link");
    var tests = new ArrayList<DynamicTest>();
    for (var link : links.asDynamicIterable()) {
      var url = link.attr("href");
      var name = link.text();
      tests.add(DynamicTest.dynamicTest(name, () -> link(url)));
    }
    return tests;
  }

  private void link(String url) {
    var link = linkFor(url);
    link.click();
    webdriver().shouldHave(urlContaining(url));
    webdriver().shouldNotHave(title("Not Found"));
    webdriver().shouldHave(titles("Axon Ivy Engine", "Axon Ivy Cockpit", "Engine Cockpit"));
  }

  private SelenideElement linkFor(String url) {
    open(EngineCockpitUtil.testViewUrl("link-factory.xhtml"));
    $("h1").shouldBe(text("CockpitLinkFactory"));
    var links = $$(".ui-link");
    for (var link : links.asDynamicIterable()) {
      if (link.attr("href").equals(url)) {
        return link;
      }
    }
    fail("No link found for url " + url);
    return null;
  }

  @Test
  void links() {
    open(EngineCockpitUtil.testViewUrl("link-factory.xhtml"));
    $("h1").shouldBe(text("CockpitLinkFactory"));
    var links = $$(".ui-link");
    links.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(7));
  }

  private ObjectCondition<WebDriver> titles(String... titles) {
    return new Titles(titles);
  }

  private static final class Titles implements ObjectCondition<WebDriver> {

    private final List<String> expectedTitles;

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
