package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestNotificationChannels {

  static final String TABLE_ID = "channelsTable";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toNotificationChannels();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void channelsInTable() {
    Table table = new Table(By.id("securitySystems:securitySystemTabView:" +
            Tab.SECURITY_SYSTEM.getSelectedTabIndex() + ":tableForm:" + TABLE_ID), true);
    table.firstColumnShouldBe(size(2));
    table.firstColumnShouldBe(CollectionCondition.exactTexts("Web", "Email"));
  }

  @Test
  void channelFilter() {
    Table table = new Table(By.id("securitySystems:securitySystemTabView:" +
            Tab.SECURITY_SYSTEM.getSelectedTabIndex() + ":tableForm:" + TABLE_ID), true);
    table.firstColumnShouldBe(size(2));

    table.search("Web");
    table.firstColumnShouldBe(size(1));

    table.firstColumnShouldBe(CollectionCondition.exactTexts("Web"));
  }
}
