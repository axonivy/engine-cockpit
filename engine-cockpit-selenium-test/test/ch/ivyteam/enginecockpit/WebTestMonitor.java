package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestMonitor
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void testMontorContent()
  {
    Navigation.toResourcesMonitor();
    $$(".ui-panel").shouldHave(size(4));
  }
  
  @Test
  void testLogsContent()
  {
    Navigation.toLogs();
    $$(".ui-panel").shouldHave(size(5));
  }
  
}
