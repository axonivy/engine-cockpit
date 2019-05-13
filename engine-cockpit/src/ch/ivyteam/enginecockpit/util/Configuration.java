package ch.ivyteam.enginecockpit.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.configuration.restricted.Property;

@SuppressWarnings("restriction")
public class Configuration
{
  private static IConfiguration config = IConfiguration.get();
  
  public static Optional<String> get(String key)
  {
    return config.get(key);
  }
  
  public static <T> Optional<T> get(String key, Class<T> type)
  {
    return config.get(key, type);
  }
  
  public static String getOrDefault(String key)
  {
    return config.getOrDefault(key);
  }
  
  public static <T> T getOrDefault(String key, Class<T> type)
  {
    return config.getOrDefault(key, type);
  }
  
  public static Collection<String> getNames(String key)
  {
    return config.getNames(key);
  }
  
  public static Map<String, String> getMap(String key)
  {
    return config.getMap(key);
  }
  
  public static List<Property> getProperties()
  {
    return config.getProperties();
  }
  
  public static void set(String key, Object value)
  {
    config.set(key, value);
  }
  
  public static void remove(String key)
  {
    config.remove(key);
  }
  
}
