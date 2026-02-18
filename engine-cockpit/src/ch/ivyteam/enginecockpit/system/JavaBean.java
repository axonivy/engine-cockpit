package ch.ivyteam.enginecockpit.system;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class JavaBean {
  private final String name;
  private final String version;
  private final String vendor;
  private final String home;
  private final List<String> jvmArgs;

  public JavaBean() {
    name = System.getProperty("java.vm.name");
    version = System.getProperty("java.version") + " (" + System.getProperty("java.vm.version") + ")";
    vendor = System.getProperty("java.vendor");
    home = System.getProperty("java.home");
    jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public String getVendor() {
    return vendor;
  }

  public String getHome() {
    return home;
  }

  public List<Object> getProperties() {
    return Arrays.asList(System.getProperties().entrySet().toArray());
  }

  public List<String> getJvmArguments() {
    return jvmArgs;
  }
}
