package ch.ivyteam.enginecockpit;

import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import io.github.bonigarcia.seljup.Options;
import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class WebBase
{

  @Options
  FirefoxOptions firefoxOptions = new FirefoxOptions();
  {
    FirefoxBinary binary = new FirefoxBinary();
    binary.addCommandLineOptions("--headless");
    firefoxOptions.setBinary(binary);
  }
  
  public static String getAdminUser()
  {
    return EngineCockpitUrl.isDesignerApp() ? "Developer" : "admin";
  }
  
  public static void webAssertThat(WebTest test)
  {
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class).untilAsserted(test::run);
  }
  
  @FunctionalInterface
  public interface WebTest
  {
    void run();
  }
}
