package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.getAppName;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
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

  @Test
  void appContextFilters() {
    openAppContext();
    $(By.partialLinkText("Filters")).click();
    $(By.id("form:tabs:filterTable")).shouldHave(text("All"));
  }

  @Test
  void webServicesLink() {
    openAppContext();
    $(By.linkText("/ws/*")).click();
    assertCurrentUrlContains("webserviceprocesses.xhtml");
    assertThat(Tab.APP.getSelectedTab()).isEqualTo(getAppName());
  }

  @Test
  void restServicesLink() {
    openAppContext();
    $(By.linkText("/api/*")).click();
    assertCurrentUrlContains("restservices.xhtml");
    $("#apiBrowser").shouldHave(attributeMatching("src", ".*primaryName=" + getAppName()));
  }

  private static void openAppContext() {
    $x("//div[@id='form:contextTable']//tbody/tr[normalize-space(td[2])='Application' and td[3]/a[text()='"
        + getAppName() + "']]/td[1]/a").click();
    assertCurrentUrlContains("monitor-webapp.xhtml");
  }
}
