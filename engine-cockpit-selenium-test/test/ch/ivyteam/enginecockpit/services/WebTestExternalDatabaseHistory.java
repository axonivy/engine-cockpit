package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.runExternalDbQuery;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestExternalDatabaseHistory
{
  
  @BeforeEach
  void beforeEach()
  {
    runExternalDbQuery();
    login();
    Navigation.toExternalDatabaseDetail("realdb");
  }
  
  @Test
  void testConnectionAndHistory()
  {
    new Table(By.id("databaseConnectionForm:databaseConnectionsTable")).firstColumnShouldBe(sizeGreaterThan(0));
    new Table(By.id("databaseExecHistoryForm:databaseExecHistoryTable")).firstColumnShouldBe(sizeGreaterThan(0));
  }
}
