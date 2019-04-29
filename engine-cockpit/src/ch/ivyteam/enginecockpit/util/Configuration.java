package ch.ivyteam.enginecockpit.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.configuration.restricted.Property;
import ch.ivyteam.util.crypto.CryptoUtil;

@SuppressWarnings("restriction")
public class Configuration
{
  public static Optional<String> get(String key)
  {
    return IConfiguration.get().get(key);
  }
  
  public static Collection<String> getNames(String key)
  {
    return IConfiguration.get().getNames(key);
  }
  
  public static Map<String, String> getMap(String key)
  {
    return IConfiguration.get().getMap(key);
  }
  
  public static List<Property> getProperties()
  {
    return IConfiguration.get().getProperties();
  }
  
  public static void set(String key, Object value)
  {
    IConfiguration.get().set(key, value);
  }
  
  public static void remove(String key)
  {
    IConfiguration.get().remove(key);
  }
  
  public static String encrpyt(String value)
  {
    if (StringUtils.isBlank(value))
    {
      return "";
    }
    try
    {
      return "${decrypt:" + CryptoUtil.encrypt(value) + "}";
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
