package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
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

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestPmvDetail
{
  private static final String APP = isDesigner() ? DESIGNER : "demo-portal";
  private static final String PM = "PortalTemplate";
  private static final String PMV = "PortalTemplate$1";
  private static final String RESOLVED = "done";  

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
    dependenciesResolved("ACTIVE");
    Navigation.toApplications();
    deactivatePortalKit();
    Navigation.toPmvDetail(APP, PM, PMV);
    dependenciesResolved("INACTIVE");
  }

  private void deactivatePortalKit()
  {
    $$(".activity-name").find(text(APP)).parent().parent().find(".ui-treetable-toggler").shouldBe(visible).click();
    $$(".activity-name").find(text("PortalKit")).parent().parent().find(".ui-treetable-toggler").shouldBe(visible).click();
    var portalKitVersionId = "card:form:tree:" + $$(".activity-name").find(text("PortalKit$1")).parent().parent().parent().shouldBe(visible).attr("data-rk");
    $(By.id(portalKitVersionId + ":deactivateButton")).shouldBe(visible).click();
  }

  private void dependenciesResolved(String portalKitActivityState)
  {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(texts("AxonIvyExpress$1", "PortalExamples$1"));
    checkPmvEntry(depTable, "AxonIvyExpress$1", "ACTIVE");
    checkPmvEntry(depTable, "PortalExamples$1", "ACTIVE");
    
    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("PortalKit$1", "PortalStyle$1", "portal-component$1"));
    checkPmvEntry(reqTable, "PortalKit$1", portalKitActivityState);
    checkPmvEntry(reqTable, "PortalStyle$1", "ACTIVE");
    checkPmvEntry(reqTable, "portal-component$1", "ACTIVE");
    
    Table specTable = new Table(By.id("specifiedTable"));
    specTable.firstColumnShouldBe(texts("ch.ivyteam.ivy.project.portal:portalKit"));
    specTable.valueForEntryShould("ch.ivyteam.ivy.project.portal:portalKit", 3, text(RESOLVED));
  }

  private void checkPmvEntry(Table table, String entry, String activityState)
  {
    table.valueForEntryShould(entry, 2, text(RESOLVED));
    table.valueForEntryShould(entry, 4, text(activityState));
    table.valueForEntryShould(entry, 5, text("RELEASED"));
  }

}
