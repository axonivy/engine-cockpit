package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;

public class Table {
  private String id;
  private String rowNumberField;
  private String subElement = "";
  private String globalFilter;

  public Table(By by) {
    this(by, "", "data-ri");
  }

  public Table(By by, boolean withLink) {
    this(by, withLink ? "a" : "", "data-ri");
  }

  public Table(By by, String subElement) {
    this(by, subElement, "data-ri");
  }

  public Table(By by, String subElement, String rowNumberField) {
    this.id = $(by).getAttribute("id");
    this.globalFilter = id + ":globalFilter";
    this.rowNumberField = rowNumberField;
    this.subElement = subElement;
  }

  public List<String> getFirstColumnEntries() {
    return $$x(getFirstColumnSpanElement()).asDynamicIterable().stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }

  public void headerShouldBe(WebElementsCondition cond) {
    $$x(getHeaderSpanElement()).shouldBe(cond);
  }

  public void firstColumnShouldBe(WebElementsCondition cond) {
    firstColumnShouldBe(cond, 1);
  }
  public void firstColumnShouldBe(WebElementsCondition cond, int indexOfSpanElement) {
    $$x(getFirstColumnSpanElement(indexOfSpanElement)).shouldBe(cond, Duration.ofSeconds(10));
  }
  public void columnShouldBe(int col, WebElementsCondition cond) {
    $$x(getColumnSpanElement(col)).shouldBe(cond, Duration.ofSeconds(10));
  }

  public List<String> getFirstColumnEntriesForSpanClass(String span) {
    return $$x(getFirstColumnSpanElement() + "[@class='" + span + "']").asDynamicIterable().stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }

  public void valueForEntryShould(String entry, int column, WebElementCondition condition) {
    tableEntry(entry, column).should(condition);
  }

  public SelenideElement tableEntry(String entry, int column) {
    return tableEntry(entry, column, 1);
  }
  public SelenideElement tableEntry(String entry, int column, int indexOfSpanElement) {
    return $x(findColumnOverEntry(entry, indexOfSpanElement) + "/td[" + column + "]");
  }

  public SelenideElement tableEntry(int row, int column)
  {
    return $x(getBody() + "/tr[" + row + "]/td[" + column + "]");
  }


  public SelenideElement row(String entry) {
    return $x(findColumnOverEntry(entry));
  }

  public ElementsCollection rows()
  {
    return $$x(getBody()+"/tr");
  }

  public SelenideElement body() {
    return $x(getBody());
  }

  public void clickButtonForEntry(String entry, String btn) {
    $(By.id(getElementIdForEntry(entry, btn))).shouldBe(visible, enabled).click();
  }

  public void buttonMenuForEntryShouldBeVisible(String entry, String menu) {
    $(By.id(getElementIdForEntry(entry, menu))).shouldBe(visible);
  }

  private String getElementIdForEntry(String entry, String btn) {
    return id + ":" + getRowNumber(entry) + ":" + btn;
  }

  public void buttonForEntryShouldBeDisabled(String entry, String btn) {
    buttonForEntryShouldBeDisabled(entry, btn, true);
  }

  public void buttonForEntryShouldBeDisabled(String entry, String btn, boolean isDisabled) {
    var button = $(By.id(getElementIdForEntry(entry, btn)));
    if (isDisabled) {
      button.shouldHave(cssClass("ui-state-disabled"));
    } else {
      button.shouldNotHave(cssClass("ui-state-disabled"));
    }
  }

  private String getRowNumber(String entry) {
    return $x(findColumnOverEntry(entry))
            .getAttribute(rowNumberField);
  }

  private String getHeaderSpanElement() {
    return getHeader()+"/tr/th/span[1]";
  }

  private String getFirstColumnSpanElement() {
    return getFirstColumnSpanElement(1);
  }
  private String getFirstColumnSpanElement(int indexOfSpanElement) {
    return getColumnSpanElement(1, indexOfSpanElement);
  }

  private String getColumnSpanElement(int col) {
    return getColumnSpanElement(col, 1);
  }
  private String getColumnSpanElement(int col, int indexOfSpanElement) {
    if (StringUtils.isNotBlank(subElement)) {
      if (col > 1) {
        return getBody()+"/tr/td["+col+"]/" + subElement + "";
      }
      return getBody()+"/tr/td["+col+"]/" + subElement + "/span[" + indexOfSpanElement + "]";
    }
    if (col > 1) {
      return getBody()+"/tr/td["+col+"]";
    }
    return getBody()+"/tr/td["+col+"]/span";
  }

  private String getHeader() {
    return "//div[@id='" + id + "']//thead";
  }

  private String getBody() {
    return "//div[@id='" + id + "']//tbody";
  }

  private String findColumnOverEntry(String entry) {
    return findColumnOverEntry(entry, 1);
  }
  private String findColumnOverEntry(String entry, int indexOfSpanElement) {
    String parentTdFromSpan = "/../..";
    if (StringUtils.isNotBlank(subElement)) {
      parentTdFromSpan = "/../../..";
    }
    return getFirstColumnSpanElement(indexOfSpanElement) + "[text()='" + entry + "']" + parentTdFromSpan;
  }

  public void search(String search) {
    $(By.id(globalFilter)).shouldBe(visible).clear();
    $(By.id(globalFilter)).sendKeys(search);
  }

  public void searchFilterShould(WebElementCondition... conditions) {
    $(By.id(globalFilter)).should(conditions);
  }

  public String getSearchFilter() {
    return $(By.id(globalFilter)).shouldBe(visible).getAttribute("value");
  }

  public void sortByColumn(String column) {
    $x("//div[@id='" + id + "']//thead//tr//span[text()='"+column+"']").click();
  }
}
