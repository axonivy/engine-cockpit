package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createBusinessData;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
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
class WebTestSearchEngine {

  private static final String DOSSIER_INDEX = "ivy-default-businessdata-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$dossier";
  private static final String ADDRESS_INDEX = "ivy-default-businessdata-ch.ivyteam.enginecockpit.testdata.businessdata.testdatacreator$address";

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
  void info() {
    $$(".card").shouldHave(size(2));
    $(By.id("searchEngineInformation:searchEngineInfoForm:name")).shouldBe(text("ivy-opensearch"));
    $(By.id("searchEngineInformation:searchEngineInfoForm:url")).shouldBe(exactText("http://localhost:19200"));
    $(By.id("searchEngineInformation:searchEngineInfoForm:version")).shouldNotBe(empty);
    $("#searchEngineInformation\\:searchEngineInfoForm\\:state > i").shouldHave(cssClass("ti-circle-check"));
    $("#searchEngineInformation\\:searchEngineInfoForm\\:health > i").shouldHave(cssClass("ti-circle-check"));
    $(By.id("searchEngineInformation:searchEngineInfoForm:diskThreshold")).shouldHave(text("true"));
    $(By.id("searchEngineInformation:searchEngineInfoForm:watermarkLow")).shouldNotBe(empty);
    $(By.id("searchEngineInformation:searchEngineInfoForm:watermarkHigh")).shouldNotBe(empty);
    $(By.id("searchEngineInformation:searchEngineInfoForm:floodStage")).shouldNotBe(empty);
  }

  @Test
  void indicies() {
    Table table = new Table(By.id("searchEngineIndices:searchEngineIndexForm:indiciesTable"), true);
    assertThat(table.getFirstColumnEntriesForSpanClass("index-name")).hasSizeGreaterThanOrEqualTo(2)
        .contains(DOSSIER_INDEX, ADDRESS_INDEX);
    checkIndexValues(table, DOSSIER_INDEX, "10");
    checkIndexValues(table, ADDRESS_INDEX, "1");
    $(By.id("searchEngineIndices:searchEngineIndexForm:indiciesTable:indexName")).shouldBe(visible).click();
    $(By.id("exception:ajaxExceptionDialog")).shouldNotBe(visible);
  }

  @Test
  void index() {
    Navigation.toSearchIndex(ADDRESS_INDEX);
    var table = new Table(By.id("tableForm:docTable"));
    table.search("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
    $(By.id("tableForm:docTable:0:showDocument"))
        .shouldBe(not(exist));
    table.search("");
    $(By.id("tableForm:docTable:0:showDocument"))
        .shouldBe(visible)
        .click();
  }

  @Test
  void configEdit() {
    $(By.id("searchEngineInformation:searchEngineInfoForm:configSearchEngine")).click();
    assertCurrentUrlContains("systemconfig.xhtml?filter=SearchEngine");
  }

  @Test
  void queryTool() {
    $(By.id("searchEngineTool:searchEngineQueryToolModal")).shouldNotBe(visible);
    $(By.id("searchEngineInformation:searchEngineInfoForm:queryToolBtn")).click();
    assertQueryTool("GET: http://localhost:19200/", "ivy-opensearch", 3);
  }

  @Test
  void indexQueryTool() {
    $(By.id("searchEngineTool:searchEngineQueryToolModal")).shouldNotBe(visible);
    new Table(By.id("searchEngineIndices:searchEngineIndexForm:indiciesTable"), true).clickButtonForEntry(ADDRESS_INDEX, "queryToolBtn");
    assertQueryTool("GET: http://localhost:19200/" + ADDRESS_INDEX + "/", "mappings", 1);
  }

  @Test
  void reindex() {
    $(By.id("searchEngineIndices:reindexSearchEngineModel")).shouldNotBe(visible);
    new Table(By.id("searchEngineIndices:searchEngineIndexForm:indiciesTable"), true).clickButtonForEntry(DOSSIER_INDEX, "reindexBtn");
    $(By.id("searchEngineIndices:reindexSearchEngineModel")).shouldBe(visible);
    $(By.id("searchEngineIndices:reindexSearchEngineModel_title")).shouldBe(text(DOSSIER_INDEX));
    $(By.id("searchEngineIndices:reindexSearchEngineBtn")).click();
    $(By.id("searchEngineIndices:reindexSearchEngineModel")).shouldNotBe(visible);
  }

  private void assertQueryTool(String url, String responseContent, int apiCount) {
    $(By.id("searchEngineTool:searchEngineQueryToolModal")).shouldBe(visible);
    $(By.className("querytool-url")).shouldBe(exactText(url));
    $(By.id("searchEngineTool:searchEngineQueryToolForm:query_input")).shouldBe(exactValue(""));
    assertQueryToolProposal(apiCount);
    $(By.id("searchEngineTool:searchEngineQueryToolForm")).find("pre").shouldBe(empty);

    $(By.id("searchEngineTool:searchEngineQueryToolForm:runSearchEngineQueryBtn")).click();
    $(By.id("searchEngineTool:searchEngineQueryToolForm")).find("pre").shouldBe(text(responseContent));
  }

  private void assertQueryToolProposal(int apiCount) {
    $(By.id("searchEngineTool:searchEngineQueryToolForm:query_button")).click();
    $(By.id("searchEngineTool:searchEngineQueryToolForm:query_panel")).shouldBe(visible);
    $(By.id("searchEngineTool:searchEngineQueryToolForm:query_panel")).findAll("li").shouldHave(size(apiCount));
    $(By.className("querytool-url")).click();
  }

  private void checkIndexValues(Table table, String tableRow, String count) {
    table.valueForEntryShould(tableRow, 2, text(count));
    table.valueForEntryShould(tableRow, 3, text(count));
    table.valueForEntryShould(tableRow, 5, not(text("unknown")));
  }
}
