package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.exactText;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestEnvironmentSwitch {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toDatabases();
    Tab.APP.switchToTab("test");
  }

  @Test
  void testEnvironmentCount() {
    EnvironmentSwitch.getAvailableEnvs()
            .shouldBe(size(2))
            .shouldBe(texts("Default", "test"));
  }

  @Test
  void testEnvironmentSwitchAndHoldState() {
    EnvironmentSwitch.getEnv().shouldBe(exactText("Default"));
    EnvironmentSwitch.switchToEnv("test");
    EnvironmentSwitch.getEnv().shouldBe(exactText("test"));

    Navigation.toRestClients();
    EnvironmentSwitch.getEnv().shouldBe(exactText("test"));
    EnvironmentSwitch.switchToEnv("Default");
    EnvironmentSwitch.getEnv().shouldBe(exactText("Default"));

    Navigation.toWebservices();
    EnvironmentSwitch.getEnv().shouldBe(exactText("Default"));
  }
}
