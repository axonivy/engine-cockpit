package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestPmvDetail {

  private static final String APP = isDesigner() ? DESIGNER : "test-pmvs";
  private static final String PMV = "main";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toPmvDetail(APP, PMV);
  }

  @Test
  void pmvDetailContent() {
    $$(".card").shouldHave(size(3));
    $(".card", 0).shouldHave(text(PMV), text("ch.ivyteam.enginecockpit:main-pmv-test"));
  }

  @Test
  void pmvDependencies() {
    Table depTable = new Table(By.id("dependentPmvTable"), true);
    depTable.firstColumnShouldBe(textsInAnyOrder("custom"));

    Table reqTable = new Table(By.id("requriedPmvTable"), true);
    reqTable.firstColumnShouldBe(texts("core"));    
  }
}
