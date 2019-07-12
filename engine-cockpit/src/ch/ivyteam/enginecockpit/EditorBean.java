package ch.ivyteam.enginecockpit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.model.ConfigFile;
import ch.ivyteam.enginecockpit.util.UrlUtil;

@ManagedBean
@ViewScoped
public class EditorBean
{
  private List<ConfigFile> configFiles = new ArrayList<>();
  
  public EditorBean()
  {
    configFiles.addAll(Arrays.asList(UrlUtil.getConfigDir().listFiles()).stream()
            .map(file -> file.getName())
            .filter(fileName -> StringUtils.endsWith(fileName, ".yaml"))
            .map(fileName -> new ConfigFile(fileName))
            .collect(Collectors.toList()));
  }
  
  public List<ConfigFile> getConfigFiles()
  {
    return configFiles;
  }
  
}
