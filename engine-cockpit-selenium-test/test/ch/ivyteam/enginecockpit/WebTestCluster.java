package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestCluster extends WebTestBase
{
  @Test
  void testCluster()
  {
    toCluster();

    $("h1").shouldBe(text("Cluster"));
    new Table(By.className("ui-datatable"), true).firstColumnShouldBe(sizeGreaterThan(0));
    
    $("#clusterNodeDialog").shouldNotBe(visible);
    $("#card\\:form\\:clusterTable\\:0\\:clusterNode").click();
    $("#clusterNodeDialog").shouldBe(visible);
  }
  
  private void toCluster()
  {
    login();
    Navigation.toCluster();
  }
}
