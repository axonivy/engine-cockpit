package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestCluster {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toCluster();
  }

  @Test
  void cluster() {
    $("h2").shouldBe(text("Cluster"));
    new Table(By.className("ui-datatable"), true).firstColumnShouldBe(sizeGreaterThan(0));

    $("#clusterNodeDialog").shouldNotBe(visible);
    $("#clusterTable\\:0\\:clusterNode").click();
    $("#clusterNodeDialog").shouldBe(visible);
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sent Messages", "Send Processing Time", "Received Messages", "Receive Processing Time"), true);
  }

}
