package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestCluster {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toCluster();
  }

  @Test
  void nodes() {
    $("h2").shouldBe(text("Cluster"));
    new Table(By.className("ui-datatable"), true).firstColumnShouldBe(sizeGreaterThan(0));
    $(id("clusterNodeDialog")).shouldNotBe(visible);
    $(id("clusterTabView:nodesForm:clusterTable:0:clusterNode")).click();
    $(id("clusterNodeDialog")).shouldBe(visible);
  }

  @Test
  void sessions() {
    $("h2").shouldBe(text("Cluster"));
    $(By.partialLinkText("Sessions")).shouldBe(visible).click();
  }

  @Test
  void liveStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Sent Messages", "Send Processing Time", "Received Messages", "Receive Processing Time"), true);
  }
}
