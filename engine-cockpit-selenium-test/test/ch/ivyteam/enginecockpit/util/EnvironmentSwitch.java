package ch.ivyteam.enginecockpit.util;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.escapeSelector;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentSwitch {
  private static final String ENV_SWITCH = ".environment-switch";

  public static void switchToEnv(String env) {
    if (getEnv().equals(env)) {
      return;
    }
    clickOnEnvSwitch();
    $(getEnvId() + "_items").findAll("li").find(text(env)).click();
  }

  public static String getEnv() {
    return $(getEnvId() + "_label").shouldBe(visible).getText();
  }

  public static List<String> getAvailableEnvs() {
    clickOnEnvSwitch();
    return $(getEnvId() + "_items").findAll("li").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }

  private static void clickOnEnvSwitch() {
    $(getEnvId()).shouldBe(visible, enabled).scrollIntoView("{block: \"center\", inline: \"center\"}")
            .click();
    $(getEnvId() + "_items").shouldBe(visible);
  }

  private static String getEnvId() {
    return escapeSelector($$(ENV_SWITCH).find(visible).getAttribute("id"));
  }
}
