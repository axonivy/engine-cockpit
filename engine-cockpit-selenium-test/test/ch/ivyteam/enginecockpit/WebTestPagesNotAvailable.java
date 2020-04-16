package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Selenide;

@IvyWebTest
public class WebTestPagesNotAvailable
{
  
  private static Path engineDir;

  @BeforeAll
  static void setup()
  {
    Path testDir = new File(System.getProperty("user.dir")).getParentFile().toPath();
    engineDir = testDir.resolve("engine-cockpit/webContent");
  }

  @Test
  void testPagesNotAccessable()
  {
    for (Path xhtml : getSubDirectoryXhtmlFiles(engineDir))
    {
      Selenide.open(viewUrl(xhtml.toString()));
      $("#content").shouldHave(text("404"));
    }
  }
  
  private List<Path> getSubDirectoryXhtmlFiles(Path path)
  {
    try
    {
      return Files.walk(path)
              .map(engineDir::relativize)
              .filter(file -> StringUtils.startsWith(file.toString(), "view/"))
              .filter(file -> StringUtils.contains(file.toString(), "/"))
              .filter(file -> StringUtils.endsWith(file.getFileName().toString(), ".xhtml"))
              .collect(Collectors.toList());
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    return Collections.emptyList();
  }
}
