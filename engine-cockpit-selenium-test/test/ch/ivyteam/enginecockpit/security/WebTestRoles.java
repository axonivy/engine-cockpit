package ch.ivyteam.enginecockpit.security;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Test;

import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

public class WebTestRoles extends WebTestBase
{

  @Test
  void testRolesInTable()
  {
    login();
    Navigation.toRoles();
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldBe(Condition.text("Roles"));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(sizeGreaterThan(1));
    $(Tab.ACITVE_PANEL_CSS + " .ui-inputfield").sendKeys("Everybody");
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(1));
  }

}
