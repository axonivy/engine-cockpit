package ch.ivyteam.enginecockpit.system;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class JavaBean {
  List<String> jvmArgs;

  public JavaBean() {
    jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
  }

  public String getVersion() {
    return System.getProperty("java.version");
  }

  public String getVendor() {
    return System.getProperty("java.vendor");
  }

  public String getName() {
    return System.getProperty("java.vm.name");
  }

  public List<Object> getProperties() {
    return Arrays.asList(System.getProperties().entrySet().toArray());
  }

  public List<String> getJvmArguments() {
    return jvmArgs;
  }
}
