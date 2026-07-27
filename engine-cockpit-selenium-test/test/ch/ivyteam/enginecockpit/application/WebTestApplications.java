package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


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
    table.rows().shouldHave(size(3));
  }

  @Test
  void openReleasedVersions() {
    var table = applicationsTable();
    table.rows().shouldHave(size(3));

    var applicationLink = table.tableEntry(1, 2).$("a");
    applicationLink.shouldBe(visible).click();

    assertCurrentUrlContains("application-version.xhtml");
  }

  @Test
  void newAppDialog_validate() {    
    $(By.id("form:createApplicationBtn")).click();
    $(By.id("newApplication:newApplicationModal")).shouldBe(visible);
    $(By.id("newApplication:newApplicationForm:saveNewApplication")).click();
    $(By.id("newApplication:newApplicationForm:newApplicationNameMessage")).shouldBe(visible).shouldHave(text("Value is required"));
    $(By.id("newApplication:newApplicationForm:cancelNewApplication")).click();
    $(By.id("newApplication:newApplicationModal")).should(not(visible));
  }

  @Test
  void searchApplication() {
    applicationsTable().rows().shouldHave(size(3));

    $(By.id("form:tabs:securitySystemTabView:0:applicationsTable:globalFilter")).sendKeys("demo-portal");
    applicationsTable().rows().shouldHave(size(1));

    $(By.id("form:tabs:securitySystemTabView:0:applicationsTable:globalFilter")).clear();
    applicationsTable().rows().shouldHave(size(3)); 

  }

  private Table applicationsTable() {
    return new Table(By.cssSelector("[id$='applicationsTable']"), true);
  }
}
