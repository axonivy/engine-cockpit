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
     driver.findElementById(getBtnIdForEntry(entry, btn)).click();
  }

  private String getBtnIdForEntry(String entry, String btn)
  {
    return id + ":" + getRowNumber(entry) + ":" + btn;
  }
  
  public boolean buttonForEntryDisabled(String entry, String btn)
  {
    return driver.findElementById(getBtnIdForEntry(entry, btn)).isDisplayed();
  }

  private String getRowNumber(String entry)
  {
    return driver.findElementByXPath("//div[@id='" + id + "']//tbody/tr/td[1]/span[text()='" + entry + "']/../..")
            .getAttribute("data-ri");
  }
  
  
}
