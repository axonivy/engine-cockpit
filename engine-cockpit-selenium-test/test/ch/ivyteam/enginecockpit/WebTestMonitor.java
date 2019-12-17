package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestMonitor extends WebTestBase
{
  @Test
  void testMontorContent()
  {
    toMonitor();
    $$(".ui-panel").shouldHave(size(4));
  }
  
  @Test
  void testLogsContent()
  {
    toLogs();
    $$(".ui-panel").shouldHave(size(4));
  }
  
  private void toMonitor()
  {
    login();
    Navigation.toResourcesMonitor();
  }
  
  private void toLogs()
  {
    login();
    Navigation.toLogs();
  }
}
