package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestCache {

  private static final By TABLE_ID = By.id("form:cacheTable");
  private Table table;

  @BeforeAll
  static void beforeAll() {
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toCache();
    table = new Table(TABLE_ID);
  }

  @Test
  void cacheViewContent() {
    $("h2").shouldHave(text("Cache"));

    table.row("AccessControl Associations").should(exist);
    table.tableEntry("AccessControl Associations", 3).shouldHave(Condition.text("n.a."));
    table.tableEntry("AccessControl Associations", 7).shouldBe(Condition.empty);

    table.row("IntermediateEventData Entities").should(exist);
    table.tableEntry("IntermediateEventData Entities", 3).shouldHave(Condition.text("1,000"));
    table.tableEntry("IntermediateEventData Entities", 7).shouldHave(Condition.text("tti=600 s, ttl=0 s"));
    table.tableEntry("IntermediateEventData Entities", 8).$("button").click();

    table.row("WorkflowEvent Long Characaters").should(exist);
    table.tableEntry("WorkflowEvent Long Characaters", 3).shouldHave(Condition.text("n.a."));
    table.tableEntry("WorkflowEvent Long Characaters", 7).shouldHave(Condition.text("max=32768"));
  }

  @Test
  void clearAllCaches() {
    table.tableEntry("AccessControl Entities", 2).shouldNotHave(Condition.text("0"));

    $(id("form:clearAllButton")).shouldBe(visible, enabled).click();
    $(id("clearAllCachesConfirmDialog")).shouldHave(Condition.text("Are you sure you want to clear all system database caches?"));
    $(id("clearAllCachesConfirmDialog:clearAllConfirmButton")).click();

    table.tableEntry("AccessControl Entities", 2).shouldHave(Condition.text("0"));
  }
}
