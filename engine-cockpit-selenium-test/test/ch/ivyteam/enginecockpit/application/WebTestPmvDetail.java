package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
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
public class WebTestPmvDetail {
  private static final String APP = isDesigner() ? DESIGNER : "demo-portal";
  private static final String PM = "PortalTemplate";
  private static final String PMV = "PortalTemplate$1";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toPmvDetail(APP, PM, PMV);
  }

  @Test
  void pmvDetailContent() {
    $$(".card").shouldHave(size(5));

    $(".card", 0).shouldHave(text(PMV), text("ch.ivyteam.ivy.project.portal:portalTemplate"));
    $(".card", 1).findAll(".activity-state-active").shouldBe(size(2));
  }

  @Test
  void pmvDependencies() {
    dependenciesResolved("ACTIVE");
    Navigation.toApplications();
    deactivatePortalKit();
    Navigation.toPmvDetail(APP, PM, PMV);
    dependenciesResolved("INACTIVE");
    //Fix delete pmv errors first
    //deletePortalKit();
    //dependenciesNotResolved();
  }

  private void deactivatePortalKit() {
    $(By.id("form:tree:expandAll")).shouldBe(visible).click();
    var portalKitVersionId = "form:tree:" + $$(".activity-name").find(exactText("PortalKit$1")).parent()
            .parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalKitVersionId + ":deactivateButton")).shouldBe(visible).click();
  }

  private void dependenciesResolved(String portalKitActivityState) {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(texts("AxonIvyExpress$1", "portal-user-examples$1"));
    checkPmvEntry(depTable, "AxonIvyExpress$1", "ACTIVE");
    checkPmvEntry(depTable, "portal-user-examples$1", "ACTIVE");

    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("PortalKit$1", "portal-component$1"));
    checkPmvEntry(reqTable, "PortalKit$1", portalKitActivityState);
    checkPmvEntry(reqTable, "portal-component$1", "ACTIVE");

    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("ch.ivyteam.ivy.project.portal:portalKit"));
    specTable.valueForEntryShould("ch.ivyteam.ivy.project.portal:portalKit", 3, text("PortalKit$1"));
  }

  private void checkPmvEntry(Table table, String entry, String activityState) {
    table.tableEntry(entry, 2).find("i").shouldHave(cssClass("activity-state-active"));
    table.tableEntry(entry, 4).find("i").shouldHave(attribute("title", activityState));
    table.tableEntry(entry, 5).find("i").shouldHave(attribute("title", "RELEASED"));
  }

}
