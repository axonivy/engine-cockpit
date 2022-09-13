package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestPmvDetail {
  private static final String APP = isDesigner() ? DESIGNER : "demo-portal";
  private static final String PM = "portal";
  private static final String PMV = "portal$1";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toPmvDetail(APP, PM, PMV);
  }

  @Test
  void pmvDetailContent() {
    $$(".card").shouldHave(size(5));

    $(".card", 0).shouldHave(text(PMV), text("com.axonivy.portal:portal"));
    $(".card", 1).findAll(".activity-state-active").shouldBe(size(2));
  }

  @Test
  void pmvDependencies() {
    dependenciesResolved("ACTIVE");
    Navigation.toApplications();
    deactivatePortalComponent();
    Navigation.toPmvDetail(APP, PM, PMV);
    dependenciesResolved("INACTIVE");
    //Fix delete pmv errors first
    //deletePortalKit();
    //dependenciesNotResolved();
  }

  private void deactivatePortalComponent() {
    $(By.id("form:tree:expandAll")).shouldBe(visible).click();
    var portalComponentVersionId = "form:tree:" + $$(".activity-name").find(exactText("portal-components$1")).parent()
            .parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalComponentVersionId + ":deactivateButton")).shouldBe(visible).click();
  }

  private void dependenciesResolved(String portalComponentActivityState) {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(textsInAnyOrder("portal-user-examples$1", "AxonIvyExpress$1"));
    checkPmvEntry(depTable, "portal-user-examples$1", "ACTIVE");
    checkPmvEntry(depTable, "AxonIvyExpress$1", "ACTIVE");

    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("portal-components$1"));
    checkPmvEntry(reqTable, "portal-components$1", portalComponentActivityState);

    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("com.axonivy.portal:portal-components"));
    specTable.valueForEntryShould("com.axonivy.portal:portal-components", 3, text("portal-components$1"));
  }

  private void checkPmvEntry(Table table, String entry, String activityState) {
    table.tableEntry(entry, 2).find("i").shouldHave(cssClass("activity-state-active"));
    table.tableEntry(entry, 4).find("i").shouldHave(attribute("title", activityState));
    table.tableEntry(entry, 5).find("i").shouldHave(attribute("title", "RELEASED"));
  }
}
