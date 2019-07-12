package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigFile
{
private File file;
private String fileName;
  
  public ConfigFile(String fileName)
  {
    this.fileName = fileName;
    file = UrlUtil.getConfigFile(fileName);
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public String getVarName()
  {
    String name = StringUtils.replace(fileName, "-", "_");
    return StringUtils.removeEnd(name, ".yaml");
  }
  
  public String getContent()
  {
    try
    {
      return FileUtils.readLines(file, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
    }
    catch (IOException e)
    {
      return "";
    }
  }
  
  public String getKeys()
  {
    return IConfiguration.get().getMetadata().keySet().stream()
            .flatMap(key -> Arrays.asList(key.split("\\.")).stream())
            .distinct()
            .collect(Collectors.joining(","));
  }
}
