package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest(headless=false)
public class WebTestRestart {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void restart() {
    $("#restartBtn").shouldBe(visible).click();
    $("#restartDialog").shouldBe(visible);
    $(id("restart:cancel")).shouldBe(visible).click();
    $("#restartDialog").shouldNotBe(visible);
  }
}
