package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.DASHBOARD;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.HttpAsserter;

@IvyWebTest
class WebTestPages {

  private static List<String> IGNORE_PAGES = List.of(
      EngineUrl.create().app("test").path("ws/engine-cockpit-test-data/197F8BA7DD34B7A2").queryParam("WSDL", "").toUrl(),
      EngineUrl.create().app("test-ad").path("ws/engine-cockpit-test-data/197F8BA7DD34B7A2").queryParam("WSDL", "").toUrl(),
      EngineUrl.create().app("test-ad").path("test-ad/ws/engine-cockpit-test-data/197F8BA7DD34B7A2").queryParam("WSDL", "").toUrl(),
      EngineUrl.create().app("demo-portal").path("ws/engine-cockpit-test-data/197F8BA7DD34B7A2").queryParam("WSDL", "").toUrl()
  );
  private Path webContentDir;

  @BeforeEach
  void beforeEach() {
    var testDir = new File(System.getProperty("user.dir")).getParentFile().toPath();
    webContentDir = testDir.resolve("engine-cockpit/webContent");
  }

  @Test
  void deadLinks() {
    EngineCockpitUtil.login();
    var sessionId = WebDriverRunner.getWebDriver().manage().getCookieNamed("JSESSIONID");
    var url = viewUrl(DASHBOARD);
    HttpAsserter.assertThat(url, sessionId.getValue(), IGNORE_PAGES).hasNoDeadLinks();
  }

  @Test
  void pagesNotAccessable() {
    for (var xhtml : getSubDirectoryXhtmlFiles(webContentDir, this::isNotInViewFolder)) {
      var url = viewUrl(xhtml.toString());
      if (url.contains("composite") || url.contains("demo-jsfcomponents")) {
        // better skipping this one. otherwise the ivy.log is full of NPEs
        // $(".exception-content").shouldHave(text("Null Pointer"));
      } else {
        System.out.println(url);
        open(url);
        $(".exception-content").shouldHave(text("Not Found"));
      }
    }
  }

  private boolean isNotInViewFolder(Path file) {
    var name = file.toString();
    return !name.startsWith("view/") && name.contains("/");
  }

  private static List<Path> getSubDirectoryXhtmlFiles(Path basePath, Predicate<Path> filter) {
    try {
      return Files.walk(basePath)
          .map(basePath::relativize)
          .filter(filter)
          .filter(file -> file.getFileName().toString().endsWith(".xhtml"))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
