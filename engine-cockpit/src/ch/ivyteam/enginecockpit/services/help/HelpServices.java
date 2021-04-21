package ch.ivyteam.enginecockpit.services.help;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Objects;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.services.model.IService;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public abstract class HelpServices
{
  protected IConfiguration configuration;
  
  public String getTitle()
  {
    return "Service";
  }

  public String getGuideText()
  {
    return "";
  }

  public String getHelpUrl()
  {
    return UrlUtil.getEngineGuideBaseUrl() + "/configuration/files/app-yaml.html";
  }

  public abstract String getYaml();

  public static String readTemplateString(String fileName)
  {
    try (var is = HelpServices.class.getResourceAsStream(fileName))
    {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      return "";
    }
  }

  public static String parsePropertiesToYaml(Map<String, String> properties)
  {
    return properties.entrySet().stream().map(p -> p.getKey() + ": " + p.getValue())
            .collect(Collectors.joining("\n      "));
  }
  
  public static String parsePropertiesToYaml(List<Property> properties)
  {
    return properties.stream().map(p -> p.getName() + ": " + p.getValue())
            .collect(Collectors.joining("\n      "));
  }

  public static String parseFeaturesToYaml(List<String> features)
  {
    return features.stream().collect(Collectors.joining("\n      - "));
  }
  
  protected void setIfChanged(String key, Object value, Object oldValue)
  {
    if (!Objects.equal(value, oldValue))
    {
      configuration.set(key, value);
    }
  }
  
  protected void setIfPwChanged(String key, IService client)
  {
    if (client.passwordChanged())
    {
      configuration.set(key, client.getPassword());
    }
  }
}
