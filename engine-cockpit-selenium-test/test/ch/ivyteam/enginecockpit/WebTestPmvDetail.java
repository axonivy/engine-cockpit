package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
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
    $("#activity_content").shouldHave(text("ACTIVE"));
  }
  
  @Test
  void pmvDependencies()
  {
    dependenciesResolved();
    Navigation.toApplications();
    deletePortalKit();
    Navigation.toPmvDetail(APP, PM, PMV);
    dependenciesNotResolved();
  }

  private void deletePortalKit()
  {
    $$(".activity-name").find(text(APP)).parent().parent().find(".ui-treetable-toggler").shouldBe(visible).click();
    var portalKitId = "card:form:tree:" + $$(".activity-name").find(text("PortalKit")).parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalKitId + ":tasksButton")).shouldBe(visible).click();
    $(By.id(portalKitId + ":deleteBtn")).shouldBe(visible).click();
    $(By.id("card:form:deleteConfirmYesBtn")).shouldBe(visible).click();
  }
  
  private void dependenciesNotResolved()
  {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(texts("AxonIvyExpress$1", "portal-user-examples$1"));
    checkPmvEntry(depTable, "AxonIvyExpress$1");
    checkPmvEntry(depTable, "portal-user-examples$1");
    
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
    checkPmvEntry(depTable, "AxonIvyExpress$1");
    checkPmvEntry(depTable, "portal-user-examples$1");
    
    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("PortalKit$1", "PortalStyle$1"));
    checkPmvEntry(reqTable, "PortalKit$1");
    checkPmvEntry(reqTable, "PortalStyle$1");
    
    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("ch.ivyteam.ivy.project.portal:portalKit"));
    specTable.valueForEntryShould("ch.ivyteam.ivy.project.portal:portalKit", 3, text("PortalKit$1"));
  }

  private void checkPmvEntry(Table table, String entry)
  {
    table.valueForEntryShould(entry, 4, text("ACTIVE"));
    table.valueForEntryShould(entry, 5, text("RELEASED"));
  }

}
