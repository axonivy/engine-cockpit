package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizard extends WebTestBase
{
  @Test
  public void testBannerLink()
  {
    navigateToWizard();

    $("#bannerLogo").click();
    $("#wizardSteps").shouldNotBe(exist);
    $("#content > h1").shouldNotHave(text("Error"));
    $("#applicationTabView").shouldBe(exist);
    assertThat(driver.getCurrentUrl()).endsWith("/ivy/sys/info.xhtml");
  }
  
  private void navigateToWizard()
  {
    login("setup.xhtml");
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Licence"));
  }
}
