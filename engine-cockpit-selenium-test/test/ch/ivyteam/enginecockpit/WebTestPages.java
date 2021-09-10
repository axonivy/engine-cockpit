package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.HttpAsserter;

@IvyWebTest
public class WebTestPages {

  private Path webContentDir;

  @BeforeEach
  void beforeEacht() {
    Path testDir = new File(System.getProperty("user.dir")).getParentFile().toPath();
    webContentDir = testDir.resolve("engine-cockpit/webContent");
  }

  @Test
  void testExternalLinks() {
    EngineCockpitUtil.login();
    var viewDir = webContentDir.resolve("view");
    for (Path xhtml : getSubDirectoryXhtmlFiles(viewDir,
            file -> !StringUtils.endsWith(file.getFileName().toString(), "login.xhtml"))) {
      HttpAsserter.assertThat(viewUrl(xhtml.toString())).hasNoDeadLinks();
    }
  }

  @Test
  void testPagesNotAccessable() {
    for (Path xhtml : getSubDirectoryXhtmlFiles(webContentDir,
            file -> !StringUtils.startsWith(file.toString(), "view/")
                    && StringUtils.contains(file.toString(), "/"))) {
      open(viewUrl(xhtml.toString()));
      $(".exception-detail").shouldHave(text("404"));
    }
  }

  private static List<Path> getSubDirectoryXhtmlFiles(Path basePath, Predicate<Path> filter) {
    try {
      return Files.walk(basePath)
              .map(basePath::relativize)
              .filter(filter)
              .filter(file -> StringUtils.endsWith(file.getFileName().toString(), ".xhtml"))
              .collect(Collectors.toList());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return Collections.emptyList();
  }
}
