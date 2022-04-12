package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
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
import com.codeborne.selenide.ElementsCollection;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.AppTab;

@IvyWebTest
public class WebTestBusinessCalendar {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toBusinessCalendar();
    AppTab.switchToDefault();
  }

  @Test
  void testBusinessCalendarTree() {
    $(By.id(getFormId() + ":tree:0:calendarNode")).shouldBe(text("Default"));
  }

  @Test
  void testBusinessCalendarDetail() {
    Navigation.toBusinessCalendarDetail("Luzern");
    $("#weekConfigurationPanel").shouldHave(text("Week configuration"));
    $$(".box").shouldHave(size(7)).first().shouldHave(text("Monday"));
    $$(".box.free").shouldHave(texts("Saturday", "Sunday"));
    $("#weekConfigurationPanel .ui-datatable").shouldHave(
            text("morning"), text("08:00:00 - 12:00:00"),
            text("afternoon"), text("13:00:00 - 17:00:00"));
    $("#freeDatesPanel").shouldHave(text("Free days"),
            text("Christmas Day"), text("12-25"),
            text("Ascension Day"), text("easter + 39"));
  }

  @Test
  void testExpandCollapseTree() {
    getVisibleTreeNodes().shouldBe(size(3));
    $(By.id(getFormId() + ":collapseAll")).shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
    $(By.id(getFormId() + ":expandAll")).shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(3));
  }

  private ElementsCollection getVisibleTreeNodes() {
    return $(By.id(getFormId())).findAll(".ui-treenode-content").filter(visible);
  }

  private String getFormId() {
    return "apps:applicationTabView:" + AppTab.getSelectedTabIndex() + ":treeForm";
  }

}
