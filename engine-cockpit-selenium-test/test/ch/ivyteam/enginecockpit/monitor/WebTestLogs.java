package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestLogs {

  @BeforeAll
  static void beforeAll() {
    login();
  }

  @BeforeEach
  void beforeEach() {
    Navigation.toLogs();
  }

  @Test
  void view() {
    $$(".ui-panel").shouldHave(size(1));
    $$(".ui-panel-titlebar").find(text("ivy.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
    var logPanel = $$(".ui-panel-titlebar").find(text("ivy.log")).parent();
    logPanel.find(".ui-panel-title > span").click();
    logPanel.find(".ui-panel-content").shouldBe(visible);
    logPanel.find(".ui-panel-titlebar-icon").click();
    logPanel.find(".ui-panel-content").shouldNotBe(visible);
  }

  @Test
  void changeDate() {
    $("#logChooserForm\\:calendar > button").shouldBe(visible).click();
    $(By.id("ui-datepicker-div")).should(visible);
    var calendar = $(By.id("logChooserForm:calendar_input"));
    calendar.clear();
    calendar.sendKeys("2020-04-01");
    calendar.sendKeys(Keys.ENTER);
    $(By.id("noLogsMessage")).shouldBe(visible).shouldHave(text("No logs found on date:"));
  }

  @Test
  void changeFile() {
    var menu = new SelectOneMenu(By.id("logChooserForm:logFiles"));
    menu.selectItemByValue("deprecation.log");
    $$(".ui-panel-titlebar").find(text("deprecation.log")).shouldBe(visible);
  }
}
