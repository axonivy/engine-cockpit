package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.supplements.IvySelenide;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;

@IvySelenide(headless = false)
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
      Selenide.open(EngineCockpitUrl.viewUrl(xhtml.toString()));
      $("#content").shouldHave(text("500"), text("SecurityException"));
    }
  }
  
  private List<Path> getSubDirectoryXhtmlFiles(Path path)
  {
    List<Path> files = new ArrayList<>();
    for (File file : Arrays.asList(path.toFile().listFiles()))
    {
      if (file.isDirectory())
      {
        files.addAll(getSubDirectoryXhtmlFiles(file.toPath()));
      }
      else if (file.isFile() && 
              !file.getParent().equals(engineDir.toFile().getAbsolutePath()) && 
              "xhtml".equals(FilenameUtils.getExtension(file.getName())))
      {
        files.add(engineDir.relativize(file.toPath()));
      }
    }
    return files;
  }
}
