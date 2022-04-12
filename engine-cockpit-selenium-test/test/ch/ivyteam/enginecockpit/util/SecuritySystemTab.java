package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;
import java.util.stream.Collectors;

public class SecuritySystemTab {

  private static final String SECURITY_SYSTEM_TAB = "li.security-system-tab > a";
  private static final String SECURITY_SYSTEM_TAB_LI = "li.security-system-tab";
  private static final String SELECTED_SECURITY_SYSTEM_TAB = "li.security-system-tab.ui-state-active";
  public static final String ACITVE_PANEL_CSS = ".ui-tabs-panel:not(.ui-helper-hidden)";

  public static int getCount() {
    return $$(SECURITY_SYSTEM_TAB).size();
  }

  public static List<String> getTabs() {
    return $$(SECURITY_SYSTEM_TAB).stream()
            .map(e -> e.getText())
            .collect(Collectors.toList());
  }

  public static int getSelectedTabIndex() {
    for (int i = 0; i < getCount(); i++) {
      if ($$(SECURITY_SYSTEM_TAB_LI).get(i).has(cssClass("ui-state-active"))) {
        return i;
      }
    }
    return -1;
  }

  public static String getSelectedTab() {
    return $(SELECTED_SECURITY_SYSTEM_TAB).shouldBe(visible).getText();
  }

  public static void switchToDefault() {
    SecuritySystemTab.switchToTab("default");
  }

  public static void switchToTab(int index) {
    if (getSelectedTabIndex() != index) {
      $$(SECURITY_SYSTEM_TAB).get(index).click();
    }
    $$(SELECTED_SECURITY_SYSTEM_TAB).find(cssClass("ui-state-active"))
            .shouldHave(attribute("data-index", String.valueOf(index)));
  }

  public static void switchToTab(String securitySystemName) {
    if (getSelectedTab().equals(securitySystemName)) {
      return;
    }
    $$(SECURITY_SYSTEM_TAB).stream()
            .filter(e -> e.has(exactText(securitySystemName)))
            .findFirst()
            .ifPresent(app -> app.click());
    $(SELECTED_SECURITY_SYSTEM_TAB).shouldBe(exactText(securitySystemName));
  }
}
