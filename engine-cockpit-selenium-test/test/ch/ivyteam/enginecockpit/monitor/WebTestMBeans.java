package ch.ivyteam.enginecockpit.monitor;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestMBeans
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void mBeansContent()
  {
    Navigation.toMBeans();
    $$(".ui-panel").shouldHave(size(3));
    mBeanNodes().shouldHave(sizeGreaterThan(5));
  }
  
  @Test
  void selectMBean()
  {
    Navigation.toMBeans();
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Runtime");
    attributesTableRows().shouldHave(sizeGreaterThan(5));
  }

  @Test
  void traceAttribute()
  {
    Navigation.toMBeans();
    expandMBeanNodeWithText("java.lang");
    clickMBeanNodeWithText("Runtime");
    clickTraceButtonForAttribute("Uptime");
    tracesTableRows().shouldHave(size(2));
  }

  private void expandMBeanNodeWithText(String treeNodeText)
  {
    SelenideElement element = getTreeNodeWithText(treeNodeText);
    SelenideElement parent = element.parent();
    SelenideElement toggler = parent.find(".ui-tree-toggler");
    toggler.shouldBe(visible).click();
  }

  private void clickMBeanNodeWithText(String treeNodeText)
  {
    getTreeNodeWithText(treeNodeText).shouldBe(visible).click();   
  }
  
  private SelenideElement getTreeNodeWithText(String treeNodeText)
  {
    SelenideElement element = mBeanNodes().find(Condition.text(treeNodeText));
    return element;
  }

  private ElementsCollection mBeanNodes()
  {
    return $$("#mBeans .ui-treenode-content");
  }
  
  private ElementsCollection attributesTableRows()
  {
    return $$("#attributes tr");
  }

  private void clickTraceButtonForAttribute(String attributeName)
  {
    attributesTableRows().find(Condition.text(attributeName)).parent().find("button").click();
  }
  
  private ElementsCollection tracesTableRows()
  {
    return $$("#traces tr");
  }

}
