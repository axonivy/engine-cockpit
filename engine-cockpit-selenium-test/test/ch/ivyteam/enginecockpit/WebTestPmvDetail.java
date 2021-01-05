package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestPmvDetail
{
  private static final String APP = isDesigner() ? DESIGNER : "demo-portal";
  private static final String PM = "PortalTemplate";
  private static final String PMV = "PortalTemplate$1";

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toPmvDetail(APP, PM, PMV);
  }
  
  @Test
  void pmvDetailContent()
  {
    $$(".ui-panel").shouldHave(size(5));
    
    $("#info_content").shouldHave(text(PMV), text("ch.ivyteam.ivy.project.portal:portalTemplate"));
    $("#activity_content").findAll(".activity-state-active").shouldBe(size(2));
  }
  
  @Test
  void pmvDependencies()
  {
    dependenciesResolved();
    Navigation.toApplications();
    deactivatePortalKit();
    deletePortalKit();
    Navigation.toPmvDetail(APP, PM, PMV);
    dependenciesNotResolved();
  }

  private void deactivatePortalKit()
  {
    $(By.id("card:form:expandAll")).shouldBe(visible).click();
    var portalKitVersionId = "card:form:tree:" + $$(".activity-name").find(text("PortalKit$1")).parent().parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalKitVersionId + ":deactivateButton")).shouldBe(visible).click();
  }

  private void deletePortalKit()
  {
    var portalKitId = "card:form:tree:" + $$(".activity-name").find(text("PortalKit")).parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalKitId + ":tasksButton")).shouldBe(visible, enabled).click();
    $(By.id(portalKitId + ":deleteBtn")).shouldBe(visible, enabled).click();
    $(By.id("card:form:deleteConfirmYesBtn")).shouldBe(visible, enabled).click();
  }
  
  private void dependenciesNotResolved()
  {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(texts("AxonIvyExpress$1", "portal-user-examples$1"));
    checkPmvEntry(depTable, "AxonIvyExpress$1", "inactive");
    checkPmvEntry(depTable, "portal-user-examples$1", "inactive");
    
    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(empty);
    
    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("ch.ivyteam.ivy.project.portal:portalKit"));
    specTable.valueForEntryShould("ch.ivyteam.ivy.project.portal:portalKit", 3, Condition.empty);
  }

  private void dependenciesResolved()
  {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(texts("AxonIvyExpress$1", "portal-user-examples$1"));
    checkPmvEntry(depTable, "AxonIvyExpress$1", "active");
    checkPmvEntry(depTable, "portal-user-examples$1", "active");
    
    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("PortalKit$1", "PortalStyle$1"));
    checkPmvEntry(reqTable, "PortalKit$1", "active");
    checkPmvEntry(reqTable, "PortalStyle$1", "active");
    
    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("ch.ivyteam.ivy.project.portal:portalKit"));
    specTable.valueForEntryShould("ch.ivyteam.ivy.project.portal:portalKit", 3, text("PortalKit$1"));
  }

  private void checkPmvEntry(Table table, String entry, String resolved)
  {
    table.tableEntry(entry, 2).find("i").shouldHave(cssClass("activity-state-" + resolved));
    table.tableEntry(entry, 4).find("i").shouldHave(attribute("title", "ACTIVE"));
    table.tableEntry(entry, 5).find("i").shouldHave(attribute("title", "RELEASED"));
  }

}
