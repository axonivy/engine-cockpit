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

import org.openqa.selenium.By;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

public class Table
{
  private String id;
  private String rowNumberField;
  private boolean withLink = false;
  private String globalFilter;

  public Table(By by)
  {
    this(by, "data-ri");
  }
  
  public Table(By by, boolean withLink)
  {
    this(by);
    this.withLink = withLink;
  }
  
  public Table(By by, String rowNumberField)
  {
    this.id = $(by).getAttribute("id");
    this.globalFilter = id + ":globalFilter";
    this.rowNumberField = rowNumberField;
  }
  
  public List<String> getFirstColumnEntries()
  {
    return $$x(getFirstColumnSpanElement()).stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  public void firstColumnShouldBe(CollectionCondition cond)
  {
    $$x(getFirstColumnSpanElement()).shouldBe(cond, Duration.ofSeconds(10));
  }

  public List<String> getFirstColumnEntriesForSpanClass(String span)
  {
    return $$x(getFirstColumnSpanElement() + "[@class='" + span + "']").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  public void valueForEntryShould(String entry, int column, Condition condition)
  {
    tableEntry(entry, column).should(condition);
  }
  
  public SelenideElement tableEntry(String entry, int column)
  {
    return $x(findColumnOverEntry(entry) + "/td[" + column + "]");
  }
  
  public SelenideElement row(String entry)
  {
    return $x(findColumnOverEntry(entry));
  }

  public void clickButtonForEntry(String entry, String btn)
  {
    $(By.id(getElementIdForEntry(entry, btn))).shouldBe(visible, enabled).click();
  }
  
  public void buttonMenuForEntryShouldBeVisible(String entry, String menu)
  {
    $(By.id(getElementIdForEntry(entry, menu))).shouldBe(visible);
  }

  private String getElementIdForEntry(String entry, String btn)
  {
    return id + ":" + getRowNumber(entry) + ":" + btn;
  }
  
  public void buttonForEntryShouldBeDisabled(String entry, String btn)
  {
    buttonForEntryShouldBeDisabled(entry, btn, true);
  }
  
  public void buttonForEntryShouldBeDisabled(String entry, String btn, boolean isDisabled)
  {
    var button = $(By.id(getElementIdForEntry(entry, btn)));
    if (isDisabled)
    {
      button.shouldHave(cssClass("ui-state-disabled"));
    }
    else
    {
      button.shouldNotHave(cssClass("ui-state-disabled"));
    }
  }

  private String getRowNumber(String entry)
  {
    return $x(findColumnOverEntry(entry))
            .getAttribute(rowNumberField);
  }

  private String getFirstColumnSpanElement()
  {
    if (withLink)
    {
      return "//div[@id='" + id + "']//tbody/tr/td[1]/a/span[1]";
    }
    return "//div[@id='" + id + "']//tbody/tr/td[1]/span";
  }
  
  private String findColumnOverEntry(String entry)
  {
    String parentTdFromSpan = "/../..";
    if (withLink)
    {
      parentTdFromSpan = "/../../.."; 
    }
    return getFirstColumnSpanElement() + "[text()='" + entry + "']" + parentTdFromSpan;
  }
  
  public void search(String search)
  {
    $(By.id(globalFilter)).shouldBe(visible).clear();
    $(By.id(globalFilter)).sendKeys(search);
  }
  
  public String getSearchFilter()
  {
    return $(By.id(globalFilter)).shouldBe(visible).getAttribute("value");
  }
  
}
