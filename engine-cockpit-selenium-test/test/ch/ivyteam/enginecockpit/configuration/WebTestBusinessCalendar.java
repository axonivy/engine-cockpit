package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.ElementsCollection;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestBusinessCalendar {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toBusinessCalendar();
    Tab.APP.switchToDefault();
  }

  @AfterEach
  void afterEach() {
    EngineCockpitUtil.setPaaSMode(false);
  }

  @Test
  void testBusinessCalendarTree() {
    $(By.id(getFormId() + ":tree:0:calendarNode")).shouldBe(text("Default"));
  }

  @Test
  void testBusinessCalendarDetail() {
    Navigation.toBusinessCalendarDetail("Luzern");
    $$(".business-calendar-box").shouldHave(size(7)).first().shouldHave(text("Monday"));
    $$(".business-calendar-box .free").shouldHave(texts("Saturday", "Sunday"));
    $("#weekConfiguration\\:workingTimeTable").shouldHave(
        text("morning"), text("08:00:00 - 12:00:00"),
        text("afternoon"), text("13:00:00 - 17:00:00"));
    $("#freeDays\\:freeDaysTable").shouldHave(
        text("Christmas Day"), text("12-25"),
        text("Ascension Day"), text("easter + 39"));
  }

  @Test
  void paasModeHidesBusinessCalendarEditorLink() {
    setPaaSMode(true);
    Navigation.toBusinessCalendarDetail("Luzern");
    $(".editor-link").shouldNot(exist);

    setPaaSMode(false);
    Navigation.toBusinessCalendarDetail("Luzern");
    $(".editor-link").shouldBe(visible);
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
    return $(By.id(getFormId())).findAll(".business-calendar").filter(visible);
  }

  private String getFormId() {
    return "apps:applicationTabView:" + Tab.APP.getSelectedTabIndex() + ":treeForm";
  }

  private void setPaaSMode(boolean enabled) {
    EngineCockpitUtil.setPaaSMode(enabled);
    open(viewUrl("dashboard.xhtml"));
  }
}
