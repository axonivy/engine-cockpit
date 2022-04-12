package ch.ivyteam.enginecockpit.util;

import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;
import java.util.stream.Collectors;

public class AppTab {
  private static final String APPLICATION_TAB = "li.application-tab > a";
  private static final String APPLICATION_TAB_LI = "li.application-tab";
  private static final String SELECTED_APPLICATION_TAB = "li.application-tab.ui-state-active";
  public static final String ACITVE_PANEL_CSS = ".ui-tabs-panel:not(.ui-helper-hidden)";

  public static int getCount() {
    return $$(APPLICATION_TAB).size();
  }

  public static List<String> getTabs() {
    return $$(APPLICATION_TAB).stream().map(e -> e.getText()).collect(Collectors.toList());
  }

  public static int getSelectedTabIndex() {
    for (int i = 0; i < getCount(); i++) {
      if ($$(APPLICATION_TAB_LI).get(i).has(cssClass("ui-state-active"))) {
        return i;
      }
    }
    return -1;
  }

  public static String getSelectedTab() {
    return $(SELECTED_APPLICATION_TAB).shouldBe(visible).getText();
  }

  public static void switchToDefault() {
    AppTab.switchToTab(isDesigner() ? DESIGNER : "test");
  }

  public static void switchToTab(int index) {
    if (getSelectedTabIndex() != index) {
      $$(APPLICATION_TAB).get(index).click();
    }
    $$(SELECTED_APPLICATION_TAB).find(cssClass("ui-state-active"))
            .shouldHave(attribute("data-index", String.valueOf(index)));
  }

  public static void switchToTab(String appName) {
    if (getSelectedTab().equals(appName)) {
      return;
    }
    $$(APPLICATION_TAB).stream()
            .filter(e -> e.has(exactText(appName)))
            .findFirst()
            .ifPresent(app -> app.click());
    $(SELECTED_APPLICATION_TAB).shouldBe(exactText(appName));
  }
}
