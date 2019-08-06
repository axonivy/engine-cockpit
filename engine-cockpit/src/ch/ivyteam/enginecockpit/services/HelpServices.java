package ch.ivyteam.enginecockpit.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Objects;

import ch.ivyteam.enginecockpit.model.IService;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.util.Property;

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

  abstract String getYaml();

  public static String readTemplateString(String fileName)
  {
    try (InputStream is = HelpServices.class.getResourceAsStream(fileName))
    {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      return "";
    }
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
