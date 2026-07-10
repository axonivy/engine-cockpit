package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestApplications {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplications();
  }

  @Test
  void tableHasApplications() {
    var table = applicationsTable();
    table.rows().shouldHave(sizeGreaterThan(0));
  }

  @Test
  void applicationLinkOpensDetailsWithCorrectName() {
    var table = applicationsTable();
    table.rows().shouldHave(sizeGreaterThan(0));

    var applicationLink = table.tableEntry(1, 1).$("a");
    assertThat(applicationLink.getText()).isNotBlank();
    applicationLink.shouldBe(visible).click();

    assertCurrentUrlContains("application.xhtml");
    var applicationName = $("[id$='name']").shouldBe(visible).getText();
    assertThat(applicationName).isNotBlank();
  }

  @Test
  void newApplicationDialog() {    
    $(By.id("form:createApplicationBtn")).click();
    $(By.id("newApplication:newApplicationModal")).shouldBe(visible);
    var secSystemLabel = $("[id$='newApplicationSecSystemInput_label']").getText();
    assertThat(secSystemLabel).isNotBlank();
    
    $(By.id("newApplication:newApplicationForm:saveNewApplication")).click();
    $(By.id("newApplication:newApplicationForm:newApplicationNameMessage")).shouldBe(visible);
    $(By.id("newApplication:newApplicationForm:cancelNewApplication")).click();
    $(By.id("newApplication:newApplicationModal")).should(not(visible));
  }

  private Table applicationsTable() {
    return new Table(By.cssSelector("[id$='applicationsTable']"), true);
  }
}
