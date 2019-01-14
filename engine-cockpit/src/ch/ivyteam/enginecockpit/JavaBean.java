package ch.ivyteam.enginecockpit;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class JavaBean
{
  public String getVersion()
  {
    return System.getProperty("java.version");
  }

  public String getVendor()
  {
    return System.getProperty("java.vendor");
  }

  public String getName()
  {
    return System.getProperty("java.vm.name");
  }
}
