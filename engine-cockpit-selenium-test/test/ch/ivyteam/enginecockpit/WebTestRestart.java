package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
public class WebTestRestart {

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.enableRestart();
    login();
  }

  @AfterEach
  void afterEach() {
    EngineCockpitUtil.removeRestart();
  }

  @Test
  void restart() {
    $("#restartBtn").shouldBe(visible).click();
    $("#restartDialog").shouldBe(visible);
    $(id("restart:cancel")).shouldBe(visible).click();
    $("#restartDialog").shouldNotBe(visible);
  }
}
