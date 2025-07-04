package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.runExternalDbQuery;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestDatabaseHistory {

  @BeforeEach
  void beforeEach() {
    runExternalDbQuery();
    login();
    Navigation.toDatabases();
    Tab.APP.switchToTab("test");
    Navigation.toDatabaseDetail("realdb");
  }

  @AfterEach
  void cleanup() {
    EngineCockpitUtil.resetConfig();
  }

  @Test
  void connectionAndHistory() {
    new Table(By.id("databaseConnectionForm:databaseConnectionsTable"))
        .firstColumnShouldBe(sizeGreaterThan(0));
    new Table(By.id("databaseExecHistoryForm:databaseExecHistoryTable"))
        .firstColumnShouldBe(sizeGreaterThan(0));

    $(".si-copy-paste").shouldBe(visible).click();
    var copyPasteAlert = switchTo().alert();
    assertThat(copyPasteAlert.getText()).contains("Person");
    copyPasteAlert.accept();
  }
}
