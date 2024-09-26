package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
public class WebTestWizard {

  static final String ACTIVE_WIZARD_STEP = "#stepForm\\:wizardSteps li.ui-state-highlight";

  @BeforeEach
  void beforeEach() {
    navigateToStep("Licence");
    $(ACTIVE_WIZARD_STEP).should(exist);
  }

  @Test
  void bannerLink() {
    $("#bannerLogo").click();
    $("#demoMode").shouldHave(attributeMatching("href", ".*/system/engine-cockpit/faces/setup-intro.xhtml"));
    $(ACTIVE_WIZARD_STEP).shouldNot(exist);
    $("#content > h1").shouldNot(exist);
    assertCurrentUrlContains("/system/");
  }

  @Test
  void nextAndPrevStep() {
    nextStep();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Administrators"));
    prevStep();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Licence"));
  }

  @Test
  void cancelWizard() {
    cancel();
    $(ACTIVE_WIZARD_STEP).shouldNot(exist);
  }

  @Test
  void finishWizard() {
    navigateToStep("System Database");
    finish();
    $("#configErrorMessage a").shouldBe(visible, text("LICENCE")).click();
    $(ACTIVE_WIZARD_STEP).shouldBe(text("Licence"));
  }

  public static void cancel() {
    $("#cancelWizard").shouldBe(visible).click();
  }

  public static void finish() {
    $("#finishWizard").shouldBe(visible).click();
    $("#finishWizardModel").shouldBe(visible);
  }

  public static void nextStep() {
    $("#nextStep").shouldBe(visible).click();
  }

  public static void prevStep() {
    $("#prevStep").shouldBe(visible).click();
  }

  public static void navigateToStep(String step) {
    login("setup.xhtml");
    String stepIndex = $$("#stepForm\\:wizardSteps .ui-steps-title").find(text(step)).parent()
            .find(".ui-steps-number").getText();
    String activeStep = $(WebTestWizard.ACTIVE_WIZARD_STEP + " .ui-steps-title").should(exist).getText();
    if (!activeStep.equals(step)) {
      $$("#stepForm\\:wizardSteps li > a").find(text("1")).click();
      for (int i = 1; i < Integer.parseInt(stepIndex); i++) {
        nextStep();
      }
    }
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " .ui-steps-title").shouldBe(exactText(step));
  }

  public static void activeStepShouldBeOk() {
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " > a").shouldHave(not(cssClass("step-warning")),
            cssClass("step-ok"));
  }

  public static void activeStepShouldHaveWarnings() {
    $(WebTestWizard.ACTIVE_WIZARD_STEP + " > a").shouldHave(cssClass("step-warning"),
            not(cssClass("step-ok")));
  }
}
