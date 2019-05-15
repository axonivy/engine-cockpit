package ch.ivyteam.enginecockpit.services;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.util.Property;

public abstract class EditServices
{
  public String getTitle()
  {
    return "Edit Service";
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
    try
    {
      return FileUtils.readFileToString(
              new File(EditServices.class.getResource(fileName).toURI()), StandardCharsets.UTF_8);
    }
    catch (IOException | URISyntaxException e)
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
}
