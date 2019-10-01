package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestSearchEngine extends WebTestBase
{

  private static final String dossierIndex = "ivy.businessdata-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$dossier";
  private static final String addressIndex = "ivy.businessdata-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$address";

  @Test
  public void testElasticSearchInfo(FirefoxDriver driver)
  {
    toSearchEngine(driver);
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(2));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineInfoForm:name").getText())
            .startsWith("ivy-elasticsearch"));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineInfoForm:url").getText())
            .isEqualTo("http://localhost:19200"));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineInfoForm:version").getText())
            .isEqualTo("7.3.0"));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineInfoForm:state").getText())
            .isEqualTo("check"));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineInfoForm:health").getText())
            .isEqualTo("check"));
  }
  
  @Test
  public void testElasticSearchIndices(FirefoxDriver driver)
  {
    createBusinessData(driver);
    toSearchEngine(driver);
    
    Table table = new Table(driver, By.id("searchEngineIndexForm:indiciesTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("index-name")).hasSize(2)
            .contains(dossierIndex, addressIndex));
    checkIndexValues(table, dossierIndex, "10");
    checkIndexValues(table, addressIndex, "1");
  }
  
  @Test
  public void testElasticSearchConfigEdit(FirefoxDriver driver)
  {
    toSearchEngine(driver);
    driver.findElementById("searchEngineInfoForm:configSearchEngine").click();
    saveScreenshot(driver, "search_engine_config");
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("systemconfig.xhtml?filter=ElasticSearch"));
  }
  
  @Test
  public void testElasticSearchQueryTool(FirefoxDriver driver)
  {
    toSearchEngine(driver);
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineQueryToolModal").isDisplayed()).isFalse());
    driver.findElementById("searchEngineInfoForm:queryToolBtn").click();
    assertQueryTool(driver, "GET: http://localhost:19200/", "ivy-elasticsearch", 3);
  }
  
  @Test
  public void testElasticSearchIndexQueryTool(FirefoxDriver driver)
  {
    createBusinessData(driver);
    toSearchEngine(driver);
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineQueryToolModal").isDisplayed()).isFalse());
    new Table(driver, By.id("searchEngineIndexForm:indiciesTable"))
            .clickButtonForEntry(dossierIndex, "queryToolBtn");
    assertQueryTool(driver, "GET: http://localhost:19200/" + dossierIndex + "/",
            "mappings", 1);
  }
  
  @Test
  public void testElasticSearchReindex(FirefoxDriver driver)
  {
    createBusinessData(driver);
    toSearchEngine(driver);
    webAssertThat(() -> assertThat(driver.findElementById("reindexSearchEngineModel").isDisplayed()).isFalse());
    new Table(driver, By.id("searchEngineIndexForm:indiciesTable"))
            .clickButtonForEntry(dossierIndex, "reindexBtn");
    saveScreenshot(driver, "reindex");
    webAssertThat(() -> assertThat(driver.findElementById("reindexSearchEngineModel").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("reindexSearchEngineModel_title").getText())
            .contains(dossierIndex));
    driver.findElementById("reindexSearchEngineForm:reindexSearchEngineBtn").click();
    saveScreenshot(driver, "reindex_click");
    webAssertThat(() -> assertThat(driver.findElementById("reindexSearchEngineModel").isDisplayed()).isFalse());
  }
  
  private void assertQueryTool(FirefoxDriver driver, String url, String responseContent, int apiCount)
  {
    saveScreenshot(driver, "query_tool");
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineQueryToolModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("querytool-url").getText())
            .isEqualTo(url));
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineQueryToolForm:query_input")
            .getAttribute("value")).isEqualTo(""));
    assertQueryToolProposal(driver, apiCount);
    webAssertThat(() -> assertThat(driver.findElementByXPath("//form[@id='searchEngineQueryToolForm']//pre").getText())
            .isEmpty());
    
    driver.findElementById("searchEngineQueryToolForm:runSearchEngineQueryBtn").click();
    saveScreenshot(driver, "run_query");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//form[@id='searchEngineQueryToolForm']//pre").getText())
            .contains(responseContent));
  }
  
  private void assertQueryToolProposal(FirefoxDriver driver, int apiCount)
  {
    driver.findElementByXPath("//*[@id='searchEngineQueryToolForm:query']/button").click();
    saveScreenshot(driver, "query_proposals");
    webAssertThat(() -> assertThat(driver.findElementById("searchEngineQueryToolForm:query_panel").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//*[@id='searchEngineQueryToolForm:query_panel']//li"))
            .hasSize(apiCount));
    driver.findElementById("searchEngineQueryToolForm:query_input").click();
  }
  
  private void checkIndexValues(Table table, String tableRow, String count)
  {
    webAssertThat(() -> assertThat(table.getValueForEntry(tableRow, 2)).contains(count));
    webAssertThat(() -> assertThat(table.getValueForEntry(tableRow, 3)).contains(count));
    webAssertThat(() -> assertThat(table.getValueForEntry(tableRow, 4)).contains("check"));
    webAssertThat(() -> assertThat(table.getValueForEntry(tableRow, 5)).doesNotContain("unknown"));
  }
  
  private void toSearchEngine(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSearchEngine(driver);
    saveScreenshot(driver, "searchengine");
  }
}
