package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestExternalDatabaseDetail
{
  private static final String DATABASE_NAME = "test-db";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toExternalDatabaseDetail(DATABASE_NAME);
  }
  
  @Test
  void testExternalDatabaseDetailOpen()
  {
    assertCurrentUrlEndsWith("externaldatabasedetail.xhtml?databaseName=" + DATABASE_NAME);
    $$(".ui-panel").shouldHave(size(4));
    $("#databaseConfigurationForm\\:name").shouldBe(exactText(DATABASE_NAME));
  }
  
  @Test
  void testOpenExternalDatabaseHelp()
  {
    $("#breadcrumbOptions > a").shouldBe(visible).click();
    $("#helpExternalDatabaseDialog\\:helpServicesModal").shouldBe(visible);
    $(".code-block").shouldBe(text(DATABASE_NAME));
  }
  
  @Test
  void testExternalDatabaseTestConnection()
  {
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text("Error"));
  
    Navigation.toExternalDatabaseDetail("realdb");
    
    $("#connResult\\:connectionTestModel").shouldNotBe(visible);
    $("#databaseConfigurationForm\\:testDatabaseBtn").click();
    $("#connResult\\:connectionTestModel").shouldBe(visible);
    $("#connResult\\:connTestForm\\:testConnectionBtn").click();
    $("#connResult\\:connTestForm\\:resultLog_content").shouldBe(text("Successfully connected to database"));
  }
  
  @Test
  void testSaveAndResetChanges()
  {
    setConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    Selenide.refresh();
    checkConfiguration("url", "org.postgresql.Driver", "testUser", "13");
    resetConfiguration();
    Selenide.refresh();
    checkConfiguration("jdbc:mysql://localhost:3306/test-db", "com.mysql.jdbc.Driver", "user", "5");
  }
  
  @Test
  void addEditRemoveProperty()
  {
    Table properties = new Table(By.id("databasePropertiesForm:databasePropertiesTable"));
    properties.firstColumnShouldBe(size(2));
    
    $("#databasePropertiesForm\\:newServicePropertyBtn").shouldBe(visible).click();
    $("#propertyModal").shouldBe(visible);
    $("#propertyForm\\:nameInput").sendKeys("bla");
    $("#propertyForm\\:valueInput").sendKeys("value");
    $("#propertyForm\\:saveProperty").click();
    properties.firstColumnShouldBe(size(3));
    assertThat(properties.getValueForEntry("bla", 2)).isEqualTo("value");
    $("#propertyModal").shouldNotBe(visible);
    
    properties.clickButtonForEntry("bla", "editPropertyBtn");
    $("#propertyModal").shouldBe(visible);
    $("#propertyForm\\:nameInput").shouldNotBe(exist);
    $("#propertyForm\\:valueInput").shouldBe(value("value")).sendKeys("1");
    $("#propertyForm\\:saveProperty").click();
    properties.firstColumnShouldBe(size(3));
    assertThat(properties.getValueForEntry("bla", 2)).isEqualTo("value1");
    $("#propertyModal").shouldNotBe(visible);
    
    properties.clickButtonForEntry("bla", "deletePropertyBtn");
    properties.firstColumnShouldBe(size(2));
  }

  private void setConfiguration(String url, String driverName, String username, String connections)
  {
    $("#databaseConfigurationForm\\:url").clear();
    $("#databaseConfigurationForm\\:url").sendKeys(url);
    
    $("#databaseConfigurationForm\\:driver_input").clear();
    $("#databaseConfigurationForm\\:driver > button").click();
    $("#databaseConfigurationForm\\:driver_panel").shouldBe(visible);
    $x("//*[@id='databaseConfigurationForm:driver_panel']//li[text()='" + driverName + "']").click();
    
    $("#databaseConfigurationForm\\:userName").clear();
    $("#databaseConfigurationForm\\:userName").sendKeys(username);

    $("#databaseConfigurationForm\\:maxConnections_input").clear();
    $("#databaseConfigurationForm\\:maxConnections_input").sendKeys(connections);
    
    $("#databaseConfigurationForm\\:saveDatabaseConfig").click();
    $("#databaseConfigurationForm\\:databaseConfigMsg_container").shouldBe(text("Database configuration saved"));
  }
  
  private void checkConfiguration(String url, String driverName, String username, String connections)
  {
    $("#databaseConfigurationForm\\:url").shouldBe(exactValue(url));
    $("#databaseConfigurationForm\\:driver_input").shouldBe(exactValue(driverName));
    $("#databaseConfigurationForm\\:userName").shouldBe(exactValue(username));
    $("#databaseConfigurationForm\\:maxConnections_input").shouldBe(exactValue(connections));
  }
  
  private void resetConfiguration()
  {
    $("#databaseConfigurationForm\\:resetConfig").click();
    $("#databaseConfigurationForm\\:resetDbConfirmDialog").shouldBe(visible);
    $("#databaseConfigurationForm\\:resetDbConfirmYesBtn").click();
    $("#databaseConfigurationForm\\:databaseConfigMsg_container").shouldBe(text("Database configuration reset"));
  }
  
}
