package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestNotification {

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.createNotification();
    login();
    Navigation.toNotification();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void notifications() {
    var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
    notifications.tableEntry(1, 1).should(matchText(".*-.*-.*"));
    notifications.tableEntry(1, 3).should(text("new-task"));
    notifications.tableEntry(1, 4).should(text("Everybody"));

    var uuid = notifications.tableEntry(1, 1).text();
    notifications.search(uuid);
    notifications.rows().should(size(1));
    notifications.tableEntry(1, 5).click();
    $(By.id("payloadModal"))
            .should(visible)
            .should(text("taskId"));
    notifications.tableEntry(1, 1).should(matchText(".*-.*-.*"));
  }

  @Test
  void notification() {
    var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
    notifications.tableEntry(1, 1).click();

    var delivery = new Table(By.id("tableForm:deliveryTable"));
    delivery.tableEntry(1, 1).should(matchText(".*-.*-.*"));
    var uuid = delivery.tableEntry(1, 1).text();
    delivery.tableEntry(1, 3).should(text("web"));
    delivery.tableEntry(1, 6).should(matchText("#.*"));

    delivery.search("non-existing-value");
    delivery.rows()
            .should(size(1))
            .should(textsInAnyOrder("No records found"));

    delivery.search("web");
    delivery.rows().should(sizeGreaterThanOrEqual(2));

    delivery.search(uuid);
    delivery.rows().should(size(1));
  }
}
