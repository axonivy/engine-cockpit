package ch.ivyteam.enginecockpit.util;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Table
{
  private FirefoxDriver driver;
  private String id;
  private String rowNumberField;
  private boolean withLink = false;

  public Table(FirefoxDriver driver, By by)
  {
    this(driver, by, "data-ri");
  }
  
  public Table(FirefoxDriver driver, By by, boolean withLink)
  {
    this(driver, by);
    this.withLink = withLink;
  }
  
  public Table(FirefoxDriver driver, By by, String rowNumberField)
  {
    this.driver = driver;
    this.id = driver.findElement(by).getAttribute("id");
    this.rowNumberField = rowNumberField;
  }
  
  public List<String> getFirstColumnEntries()
  {
    return driver.findElementsByXPath(getFirstColumnSpanElement()).stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }

  public List<String> getFirstColumnEntriesForSpanClass(String span)
  {
    return driver.findElementsByXPath(getFirstColumnSpanElement() + "[@class='" + span + "']").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  public String getValueForEntry(String entry, int column)
  {
    return driver.findElementByXPath(findColumnOverEntry(entry) + "[" + column + "]").getText();
  }

  public void clickButtonForEntry(String entry, String btn)
  {
     driver.findElementById(getElementIdForEntry(entry, btn)).click();
  }
  
  public boolean buttonMenuForEntryVisible(String entry, String menu)
  {
    return driver.findElementById(getElementIdForEntry(entry, menu)).isDisplayed();
  }

  private String getElementIdForEntry(String entry, String btn)
  {
    return id + ":" + getRowNumber(entry) + ":" + btn;
  }
  
  public boolean buttonForEntryDisabled(String entry, String btn)
  {
    return !driver.findElementById(getElementIdForEntry(entry, btn)).isEnabled();
  }

  private String getRowNumber(String entry)
  {
    return driver.findElementByXPath(findColumnOverEntry(entry))
            .getAttribute(rowNumberField);
  }

  private String getFirstColumnSpanElement()
  {
    if (withLink)
    {
      return "//div[@id='" + id + "']//tbody/tr/td[1]/a/span";
    }
    return "//div[@id='" + id + "']//tbody/tr/td[1]/span";
  }
  
  private String findColumnOverEntry(String entry)
  {
    String parentTdFromSpan = "/../../td";
    if (withLink)
    {
      parentTdFromSpan = "/../../../td"; 
    }
    return getFirstColumnSpanElement() + "[text()='" + entry + "']" + parentTdFromSpan;
  }
  
  public void search(String search)
  {
    driver.findElementById(id + ":globalFilter").clear();
    driver.findElementById(id + ":globalFilter").sendKeys(search);
  }
  
  public String getSearchFilter()
  {
    return driver.findElementById(id + ":globalFilter").getAttribute("value");
  }
  
}
