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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Tab {

  public static final Tab SECURITY_SYSTEM = new Tab(
          "li.security-system-tab > a",
          "li.security-system-tab",
          "li.security-system-tab.ui-state-active",
          ".ui-tabs-panel:not(.ui-helper-hidden)",
          tab -> tab.switchToTab("default"));

  public static final Tab APP = new Tab(
          "li.application-tab > a",
          "li.application-tab",
          "li.application-tab.ui-state-active",
          ".ui-tabs-panel:not(.ui-helper-hidden)",
          tab -> tab.switchToTab(isDesigner() ? DESIGNER : "test"));

  private final String tab;
  private final String li;
  private final String selectedTab;
  public final String activePanelCss;
  private final Consumer<Tab> defaultSwitcher;

  public Tab(String tab, String li, String selectedTab, String activePanelCss, Consumer<Tab> defaultSwitcher) {
    this.tab = tab;
    this.li = li;
    this.selectedTab = selectedTab;
    this.activePanelCss = activePanelCss;
    this.defaultSwitcher = defaultSwitcher;
  }

  public int getCount() {
    return $$(tab).size();
  }

  public List<String> getTabs() {
    return $$(tab).stream()
            .map(e -> e.getText())
            .collect(Collectors.toList());
  }

  public int getSelectedTabIndex() {
    for (int i = 0; i < getCount(); i++) {
      if ($$(li).get(i).has(cssClass("ui-state-active"))) {
        return i;
      }
    }
    return -1;
  }

  public String getSelectedTab() {
    return $(selectedTab).shouldBe(visible).getText();
  }

  public void switchToDefault() {
    defaultSwitcher.accept(this);
  }

  public void switchToTab(int index) {
    if (getSelectedTabIndex() != index) {
      $$(tab).get(index).click();
    }
    $$(selectedTab).find(cssClass("ui-state-active"))
            .shouldHave(attribute("data-index", String.valueOf(index)));
  }

  public void switchToTab(String securitySystemName) {
    if (getSelectedTab().equals(securitySystemName)) {
      return;
    }
    $$(tab).stream()
            .filter(e -> e.has(exactText(securitySystemName)))
            .findFirst()
            .ifPresent(app -> app.click());
    $(selectedTab).shouldBe(exactText(securitySystemName));
  }
}
