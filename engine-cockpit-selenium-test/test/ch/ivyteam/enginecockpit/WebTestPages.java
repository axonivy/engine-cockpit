package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.HttpAsserter;

@IvyWebTest
class WebTestPages {

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
    var url = viewUrl("dashboard.xhtml");
    HttpAsserter.assertThat(url).hasNoDeadLinks(10, sessionId.getValue());
  }

  @Test
  void pagesNotAccessable() {
    for (var xhtml : getSubDirectoryXhtmlFiles(webContentDir, this::isNotInViewFolder)) {
      open(viewUrl(xhtml.toString()));
      $(".exception-detail").shouldHave(text("Not Found"));
    }
  }

  private boolean isNotInViewFolder(Path file) {
    var name = file.toString();
    return !StringUtils.startsWith(name, "view/") && StringUtils.contains(name, "/");
  }

  private static List<Path> getSubDirectoryXhtmlFiles(Path basePath, Predicate<Path> filter) {
    try {
      return Files.walk(basePath)
              .map(basePath::relativize)
              .filter(filter)
              .filter(file -> StringUtils.endsWith(file.getFileName().toString(), ".xhtml"))
              .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
