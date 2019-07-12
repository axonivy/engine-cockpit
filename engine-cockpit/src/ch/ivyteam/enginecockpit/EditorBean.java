package ch.ivyteam.enginecockpit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;

import ch.ivyteam.enginecockpit.util.UrlUtil;

@ManagedBean
@ViewScoped
public class EditorBean
{
  private File ivyYaml;
  
  public EditorBean()
  {
    ivyYaml = UrlUtil.getConfigFile("ivy.yaml");
  }
  
  public String getContent()
  {
    try
    {
      return FileUtils.readLines(ivyYaml, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
    }
    catch (IOException e)
    {
      return "";
    }
  }
}
