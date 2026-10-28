package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestWebApps {

  private static final String COCKPIT = "/system/engine-cockpit";

  @BeforeAll
  static void beforeAll() {
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toWebApps();
  }

  @Test
  void contexts() {
    $("h2").shouldHave(text("Web Apps"));
    var table = new Table(By.id("form:contextTable"), true);
    table.row(COCKPIT).shouldHave(text("Engine Cockpit"), text("system"), text("STARTED"));
  }

  @Test
  void servletsAndFilters() {
    $(By.linkText(COCKPIT)).click();
    assertCurrentUrlContains("monitor-webapp.xhtml");
    $("h2").shouldHave(text(COCKPIT));
    $(By.id("form:tabs:servletTable")).shouldHave(text("jakarta.faces.webapp.FacesServlet"));

    $(By.partialLinkText("Filters")).click();
    $(By.id("form:tabs:filterTable")).shouldHave(text("SecurityContextFilter"));
  }
}
