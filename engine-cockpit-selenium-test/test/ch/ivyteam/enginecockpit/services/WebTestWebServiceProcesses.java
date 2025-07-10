package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestWebServiceProcesses {

  private static final String APP = "test";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplicationDetail(APP);
    Navigation.toWebServiceProcesses();
  }

  @Test
  void webServices() {
    Tab.APP.switchToDefault();
  }
}
