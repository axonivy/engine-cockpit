package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestProject {

  private static final String APP = isDesigner() ? EngineUrl.applicationName() : "test-pmvs";
  private static final String VERSION = "1";
  private static final String PROJECT = "main";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toProject(APP, VERSION, PROJECT);
  }

  @Test
  void projectDetailContent() {
    $$(".card").shouldHave(size(3));
    $(".card", 0).shouldHave(text(PROJECT), text("ch.ivyteam.enginecockpit:main-pmv-test"));
  }

  @Test
  void projectDependencies() {
    var depTable = new Table(By.cssSelector("[id$='dependentProjectsTable']"), true);
    depTable.tableEntry(1, 1).shouldHave(text("custom"));

    var reqTable = new Table(By.cssSelector("[id$='requiredProjectTable']"), true);
    reqTable.tableEntry(1, 1).shouldHave(text("core"));
  }
}
