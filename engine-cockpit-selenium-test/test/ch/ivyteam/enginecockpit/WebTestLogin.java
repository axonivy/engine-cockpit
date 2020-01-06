package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Selenide;

public class WebTestLogin extends WebTestBase
{

  @Test
  void testLogin()
  {
    login();
    assertCurrentUrlEndsWith("dashboard.xhtml");
    assertThat(Selenide.title()).startsWith("Engine Cockpit").doesNotContain("Login");
    $("#sessionUserName").shouldBe(exactText(getAdminUser())); 
  }
  
  @Test
  void testLoginInvalid()
  {
    Selenide.open(viewUrl("login.xhtml"));
    $("#loginForm\\:userName").shouldBe(visible).clear();
    $("#loginForm\\:password").shouldBe(visible).clear();
    $("#loginForm\\:login").shouldBe(visible).click();
    $("#loginForm\\:userNameMessage").shouldBe(exactText("Value is required."));
    $("#loginForm\\:passwordMessage").shouldBe(exactText("Value is required."));
    
    $("#loginForm\\:userName").sendKeys(getAdminUser());
    $("#loginForm\\:login").click();
    $("#loginForm\\:passwordMessage").shouldBe(exactText("Value is required."));
    
    $("#loginForm\\:password").sendKeys("test");
    $("#loginForm\\:login").click();
    $("#loginForm\\:passwordMessage").shouldBe(empty);
    $("#loginForm\\:loginMessage").shouldBe(visible);
  }
  
  @Test
  void testLogout()
  {
    login();
    logout();
    assertLoginPageVisible();
    Selenide.open(viewUrl("dashboard.xhtml"));
    $("#sessionUserName").shouldBe(exactText(getAdminUser())); 
  }

  private void assertLoginPageVisible()
  {
    assertCurrentUrlEndsWith("login.xhtml");
    assertThat(Selenide.title()).contains("Login");
  }
  
  private void logout()
  {
    $("#sessionUser > a").click();
    $("#sessionLogoutBtn").shouldBe(visible).click();
  }
}
