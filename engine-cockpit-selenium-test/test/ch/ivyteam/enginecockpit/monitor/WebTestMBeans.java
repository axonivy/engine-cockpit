package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestMBeans
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toMBeans();
  }
  
  @Test
  void mBeansContent()
  {
    $$(".ui-panel").shouldHave(size(3));
    mBeanNodes().shouldHave(sizeGreaterThan(5));
  }
  
  @Test
  void selectMBean()
  {
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Runtime");
    assertThat(attributesTableRows().size()).isGreaterThan(5);
  }

  @Test
  void traceAttributes()
  {
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Runtime");
    
    clickTraceButtonForAttribute("Uptime");
    assertThat(tracesTableRows().size()).isEqualTo(1);
    
    clickTraceButtonForAttribute("StartTime");
    assertThat(tracesTableRows().size()).isEqualTo(2);

  }

  @Test
  void removeTrace()
  {
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Runtime");
    clickTraceButtonForAttribute("Uptime");
    assertThat(tracesTableRows().size()).isEqualTo(1);
    
    clickRemoveTraceButtonForAttribute("Uptime");
    assertThat(tracesTableRows()).isEmpty();
  }
  
  public static void toMBeans()
  {
    Navigation.toMBeans();
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Memory");
    clickTraceButtonForAttribute("HeapMemoryUsage");
    assertThat(tracesTableRows().size()).isEqualTo(4);
    
    try
    {
      TimeUnit.SECONDS.sleep(20);
    }
    catch(InterruptedException ex)
    {
      fail("sleep", ex);
    }
  }


  private static void expandMBeanNodeWithText(String treeNodeText)
  {
    getTreeNodeWithText(treeNodeText)
        .parent()
        .find(".ui-tree-toggler")
        .shouldBe(visible)
        .click();
  }

  private static void clickMBeanNodeWithText(String treeNodeText)
  {
    getTreeNodeWithText(treeNodeText).shouldBe(visible).click();   
  }
  
  private static SelenideElement getTreeNodeWithText(String treeNodeText)
  {
    return mBeanNodes().find(Condition.text(treeNodeText));
  }

  private static ElementsCollection mBeanNodes()
  {
    return $$("#mBeans .ui-treenode-content");
  }
  
  private static List<String> attributesTableRows()
  {
    return new Table(By.id("attributes")).getFirstColumnEntries();
  }

  private static void clickTraceButtonForAttribute(String attributeName)
  {
    new Table(By.id("attributes")).clickButtonForEntry(attributeName, "addTrace");
  }
  
  private static List<String> tracesTableRows()
  {
    return new Table(By.id("traces")).getFirstColumnEntries();
  }
  
  private static void clickRemoveTraceButtonForAttribute(String attributeName)
  {
    new Table(By.id("traces")).clickButtonForEntry(attributeName, "removeTrace");
  }
}
