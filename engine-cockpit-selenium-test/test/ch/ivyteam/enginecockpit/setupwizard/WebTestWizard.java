package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizard extends WebTestBase
{
  static final String ACTIVE_WIZARD_STEP = "#stepForm\\:wizardSteps li.ui-state-highlight";
  
  @Test
  public void testBannerLink()
  {
    navigateToWizard();

    $("#bannerLogo").click();
    $("#applicationTabView").should(exist);
    $(ACTIVE_WIZARD_STEP).shouldNot(exist);
    $("#content > h1").shouldNot(exist);
    assertCurrentUrlEndsWith("/ivy/sys/info.xhtml");
  }
  
  @Test
  public void testNextAndPrevStep()
  {
    navigateToStep("Licence");
    nextStep();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Administrators"));
    prevStep();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Licence"));
  }
  
  @Test
  public void testCancelWizard()
  {
    navigateToStep("Licence");
    cancelWizard();
    $(ACTIVE_WIZARD_STEP).shouldNot(exist);
  }
  
  @Test
  public void testFinishWizard()
  {
    navigateToStep("System Database");
    finishWizard();
    $("#configErrorMessage a").shouldBe(visible, text("LICENCE")).click();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Licence"));
  }
  
  //TODO: add tests for finish dialog warnings
  //TODO: add tests for start with warning step
  
  private void navigateToWizard()
  {
    login("setup.xhtml");
    $(ACTIVE_WIZARD_STEP).should(exist);
  }
  
  public static void cancelWizard()
  {
    $("#cancelWizard").shouldBe(visible).click();
  }
  
  public static void finishWizard()
  {
    $("#finishWizard").shouldBe(visible).click();
    $("#finishWizardModel").shouldBe(visible);
  }
  
  public static void nextStep()
  {
    $("#nextStep").shouldBe(visible).click();
  }
  
  public static void prevStep()
  {
    $("#prevStep").shouldBe(visible).click();
  }
  
  public static void navigateToStep(String step)
  {
    login("setup.xhtml");
    String stepIndex = $$("#stepForm\\:wizardSteps .ui-steps-title").find(text(step)).parent().find(".ui-steps-number").getText();
    String activeStep = $(WebTestWizard.ACTIVE_WIZARD_STEP + " .ui-steps-title").should(exist).getText();
    if (!activeStep.equals(step))
    {
      $$("#stepForm\\:wizardSteps li > a").find(text("1")).click();
      for (int i = 1; i < Integer.parseInt(stepIndex); i++)
      {
        nextStep();
      }
    }
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " .ui-steps-title").shouldBe(exactText(step));
  }
  
  public static void activeStepShouldBeOk()
  {
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " > a").shouldHave(not(cssClass("step-warning")), cssClass("step-ok"));
  }
  
  public static void activeStepShouldHaveWarnings()
  {
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " > a").shouldHave(cssClass("step-warning"), not(cssClass("step-ok")));
  }
}
