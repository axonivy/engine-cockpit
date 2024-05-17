package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest(headless=false)
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
      var page = link.text();
      link.click();
      Selenide.Wait()
          .withMessage(() -> "Page '" + page + "' title must be 'Engine Cockpit' or 'Axon Ivy Engine' but is '"+Selenide.title()+"'")
          .until(driver -> driver.getTitle().equals("Engine Cockpit") || driver.getTitle().equals("Axon Ivy Engine"));
      Selenide.back();
      links = $$(".ui-link");
    }
  }
}
