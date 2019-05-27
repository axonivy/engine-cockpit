package ch.ivyteam.enginecockpit.util;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

public class EngineCockpitUrl
{
  private static final String DESIGNER_APP = "designer";
  private URIBuilder uriBuilder;

  private EngineCockpitUrl(String... parts)
  {
    try
    {
      String url = StringUtils.join(parts, "/");
      uriBuilder = new URIBuilder(url);
    }
    catch (URISyntaxException e)
    {
      throw new IllegalArgumentException(e);
    }
  }

  public static EngineCockpitUrl createView(String xhtmlView)
  {
    return new EngineCockpitUrl(base(), "faces/view", applicationName(), pmvName(), xhtmlView);
  }

  public static String viewUrl(String xhtmlView)
  {
    return createView(xhtmlView).build();
  }

  public EngineCockpitUrl queryParam(String name, String value)
  {
    uriBuilder.addParameter(name, value);
    return this;
  }

  public String build()
  {
    return uriBuilder.toString();
  }

  public static String base()
  {
    return StringUtils.removeEnd(System.getProperty("test.engine.url", "http://localhost:8081/ivy"), "/");
  }

  public static String applicationName()
  {
    return System.getProperty("test.engine.app", DESIGNER_APP);
  }
  
  public static boolean isDesignerApp()
  {
    return System.getProperty("test.engine.app", DESIGNER_APP).equals(DESIGNER_APP);
  }

  public static String pmvName()
  {
    return System.getProperty("test.engine.pmv", "engine-cockpit");
  }
}
