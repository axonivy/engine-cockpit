package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestCluster
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toCluster();
  }
  
  @Test
  void testCluster()
  {
    $("h1").shouldBe(text("Cluster"));
    new Table(By.className("ui-datatable"), true).firstColumnShouldBe(sizeGreaterThan(0));
    
    $("#clusterNodeDialog").shouldNotBe(visible);
    $("#card\\:form\\:clusterTable\\:0\\:clusterNode").click();
    $("#clusterNodeDialog").shouldBe(visible);
  }
  
}
