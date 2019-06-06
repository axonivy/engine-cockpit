package ch.ivyteam.enginecockpit.util;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Table
{
  private FirefoxDriver driver;
  private String id;

  public Table(FirefoxDriver driver, By by)
  {
    this.driver = driver;
    id = driver.findElement(by).getAttribute("id");
  }
  
  public List<String> getFirstColumnEntries()
  {
    return driver.findElementsByXPath("//div[@id='" + id + "']//tbody/tr/td[1]/span").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  public String getValueForEntry(String entry, int column)
  {
    return driver.findElementByXPath("//div[@id='" + id + "']//tbody/tr/td[1]/span[text()='" + entry + "']/../../td[" + column + "]")
            .getText();
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
    return driver.findElementByXPath("//div[@id='" + id + "']//tbody/tr/td[1]/span[text()='" + entry + "']/../..")
            .getAttribute("data-ri");
  }
  
  public void search(String search)
  {
    driver.findElementById(id + ":globalFilter").clear();
    driver.findElementById(id + ":globalFilter").sendKeys(search);
  }
  
  
}
