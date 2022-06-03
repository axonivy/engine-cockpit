package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createBusinessData;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSearchEngine {

  private static final String dossierIndex = "ivy.businessdata-default-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$dossier";
  private static final String addressIndex = "ivy.businessdata-default-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$address";

  @BeforeAll
  static void setup() {
    createBusinessData();
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSearchEngine();
  }

  @Test
  public void testElasticSearchInfo() {
    $$(".card").shouldHave(size(2));
    $("#searchEngineInfoForm\\:name").shouldBe(text("ivy-elasticsearch"));
    $("#searchEngineInfoForm\\:url").shouldBe(exactText("http://localhost:19200"));
    $("#searchEngineInfoForm\\:version").shouldBe(exactText("7.17.1"));
    $("#searchEngineInfoForm\\:state > i").shouldHave(cssClass("si-check-circle-1"));
    $("#searchEngineInfoForm\\:health > i").shouldHave(cssClass("si-check-circle-1"));
  }

  @Test
  public void testElasticSearchIndices() {
    Table table = new Table(By.id("searchEngineIndexForm:indiciesTable"));
    assertThat(table.getFirstColumnEntriesForSpanClass("index-name")).hasSize(2)
            .contains(dossierIndex, addressIndex);
    checkIndexValues(table, dossierIndex, "10");
    checkIndexValues(table, addressIndex, "1");
  }

  @Test
  public void testElasticSearchConfigEdit() {
    $("#searchEngineInfoForm\\:configSearchEngine").click();
    assertCurrentUrlContains("systemconfig.xhtml?filter=ElasticSearch");
  }

  @Test
  public void testElasticSearchQueryTool() {
    $("#searchEngineQueryToolModal").shouldNotBe(visible);
    $("#searchEngineInfoForm\\:queryToolBtn").click();
    assertQueryTool("GET: http://localhost:19200/", "ivy-elasticsearch", 3);
  }

  @Test
  public void testElasticSearchIndexQueryTool() {
    $("#searchEngineQueryToolModal").shouldNotBe(visible);
    new Table(By.id("searchEngineIndexForm:indiciesTable"))
            .clickButtonForEntry(dossierIndex, "queryToolBtn");
    assertQueryTool("GET: http://localhost:19200/" + dossierIndex + "/", "mappings", 1);
  }

  @Test
  public void testElasticSearchReindex() {
    $("reindexSearchEngineModel").shouldNotBe(visible);
    new Table(By.id("searchEngineIndexForm:indiciesTable")).clickButtonForEntry(dossierIndex, "reindexBtn");
    $("#reindexSearchEngineModel").shouldBe(visible);
    $("#reindexSearchEngineModel_title").shouldBe(text(dossierIndex));
    $("#reindexSearchEngineBtn").click();
    $("#reindexSearchEngineModel").shouldNotBe(visible);
  }

  private void assertQueryTool(String url, String responseContent, int apiCount) {
    $("#searchEngineQueryToolModal").shouldBe(visible);
    $(".querytool-url").shouldBe(exactText(url));
    $("#searchEngineQueryToolForm\\:query_input").shouldBe(exactValue(""));
    assertQueryToolProposal(apiCount);
    $("#searchEngineQueryToolForm pre").shouldBe(empty);

    $("#searchEngineQueryToolForm\\:runSearchEngineQueryBtn").click();
    $("#searchEngineQueryToolForm pre").shouldBe(text(responseContent));
  }

  private void assertQueryToolProposal(int apiCount) {
    $("#searchEngineQueryToolForm\\:query > button").click();
    $("#searchEngineQueryToolForm\\:query_panel").shouldBe(visible);
    $$("#searchEngineQueryToolForm\\:query_panel li").shouldHave(size(apiCount));
    $("#searchEngineQueryToolForm\\:query_input").click();
  }

  private void checkIndexValues(Table table, String tableRow, String count) {
    table.valueForEntryShould(tableRow, 2, text(count));
    table.valueForEntryShould(tableRow, 3, text(count));
    table.valueForEntryShould(tableRow, 5, not(text("unknown")));
  }

}
