package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestLogs {

  @BeforeAll
  static void beforeAll() {
    login();
    Navigation.toLogs();
  }

  @Test
  void logsViewContent() {
    Navigation.toLogs();
    $$(".ui-panel").shouldHave(sizeGreaterThanOrEqual(1));
    SelenideElement ivyLogPanel = $$(".ui-panel-titlebar").find(text("ivy.log")).parent();
    ivyLogPanel.find(".ui-panel-title > span").click();
    ivyLogPanel.find(".ui-panel-content").shouldBe(visible);
    ivyLogPanel.find(".ui-panel-titlebar-icon").click();
    ivyLogPanel.find(".ui-panel-content").shouldNotBe(visible);
  }

  @Test
  void logsChangeDate() {
    var deprecationLogPanel = $$(".ui-panel-titlebar").find(text("deprecation.log")).parent();
    deprecationLogPanel.find(".ui-panel-title > span").click();
    deprecationLogPanel.find(".ui-panel-content pre").shouldHave(text("'deprecation.log'"));

    $("#logDateForm\\:calendar > button").shouldBe(visible).click();
    $("#ui-datepicker-div").should(visible);
    $("#logDateForm\\:calendar_input").clear();
    $("#logDateForm\\:calendar_input").sendKeys("2020-04-01");
    $("#logDateForm\\:calendar_input").sendKeys(Keys.ENTER);
    deprecationLogPanel.shouldNotBe(visible);
    $(By.id("noLogsMessage")).shouldBe(visible).shouldHave(text("No logs found on date:"));
  }

}
