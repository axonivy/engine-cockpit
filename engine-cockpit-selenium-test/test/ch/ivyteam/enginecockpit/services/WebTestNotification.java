package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest(headless=false)
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
  void notifications_receiver() {
    var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
    notifications.tableEntry(1, 4).should(text("Everybody")).click();
    Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining("roledetails.xthml"));
  }

  @Test
  void delivery() {
    var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
    notifications.tableEntry(1, 1).click();

    var delivery = new Table(By.id("tableForm:deliveryTable"));
    delivery.tableEntry(1, 1).should(matchText(".*-.*-.*"));
    var uuid = delivery.tableEntry(1, 1).text();
    delivery.tableEntry(1, 2).shouldBe(text("Web"));
    delivery.tableEntry(1, 3).should(matchText("Developer|foo|bar|jon|guest|demo|admin|disableduser"));
    if (delivery.tableEntry(1, 3).text().equals("Developer")) {
      delivery.tableEntry(1, 4).shouldBe(text("DELIVERED"));
      delivery.tableEntry(1, 5).shouldBe(not(empty));
    } else {
      delivery.tableEntry(1, 4).should(text("PENDING"));
      delivery.tableEntry(1, 5).shouldBe(empty);
    }
    delivery.tableEntry(1, 6).shouldBe(empty);
    delivery.tableEntry(1, 7).should(text("false"));
    delivery.tableEntry(1, 8).shouldBe(empty);

    delivery.search("non-existing-value");
    delivery.rows()
            .should(size(1))
            .should(textsInAnyOrder("No records found"));

    delivery.search("Web");
    delivery.rows().should(sizeGreaterThanOrEqual(1));

    delivery.search(uuid);
    delivery.rows().should(size(1));
  }

  @Test
  void delivery_receiver() {
    var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
    notifications.tableEntry(1, 1).click();
    var delivery = new Table(By.id("tableForm:deliveryTable"));
    delivery.tableEntry(1, 3).should(matchText("Developer|foo|bar|jon|guest|demo|admin|disableduser")).click();
    Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining("userdetail.xhtml"));
  }

  @Test
  void failed_delivery() {
    enableMailChannel(true);
    try {
      EngineCockpitUtil.createNotification();
      login();
      Navigation.toNotification();
      Tab.SECURITY_SYSTEM.switchToDefault();

      var notifications = new Table(By.id("tabs:securitySystemTabView:0:form:notificationTable"), true);
      notifications.tableEntry(1, 1).click();

      var delivery = new Table(By.id("tableForm:deliveryTable"));
      delivery.search("mail");
      delivery.rows().should(sizeGreaterThanOrEqual(1));
      delivery.tableEntry(1, 4).should(text("ERROR"));
      delivery.tableEntry(1, 8).shouldBe(text("1"));
      delivery.tableEntry(1, 8).shouldBe(Condition.matchText("[0-9]+ [smhd]"));
      delivery.tableEntry(1, 8).click();
      var errorDetails = $(By.id("tableForm:deliveryTable:0:errorDetails"));
      errorDetails.shouldHave(text("Error Information"));
      errorDetails.shouldHave(text("Failed deliver attemps so far:"));
      errorDetails.shouldHave(text("Next retry to deliver:"));
      errorDetails.shouldHave(text("Last Error:"));
      errorDetails.shouldHave(text("Connection refused"));
    } finally {
      enableMailChannel(false);
    }

  }

  private void enableMailChannel(boolean enable) {
    Navigation.toNotificationChannelDetail("mail");
    var enabledCheckbox = PrimeUi.selectBooleanCheckbox(By.id("form:enabled"));
    if (enable) {
      enabledCheckbox.setChecked();
    } else {
      enabledCheckbox.removeChecked();
    }
    $(By.id("save")).click();
  }
}
