package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.Optional;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityContext;

@SuppressWarnings("restriction")
public class SecuritySystem
{

  private String securitySystemProvider;
  private String securitySystemName;
  private long id;
//  private String appName;
  private List<String> appNames;
  private int usersCount;
  private int rolesCount;

//  public SecuritySystem(ISecurityContext securityContext, String appName)
//  {
//    securitySystemName = IConfiguration.get().get("Applications." + appName + ".SecuritySystem")
//            .orElse(securityContext.getExternalSecuritySystemName());
//    securitySystemProvider = securityContext.getExternalSecuritySystemProvider().getProviderName();
//    id = securityContext.getId();
//    this.appName = appName;
//    this.usersCount = securityContext.getUsers().size() - 1;
//    this.rolesCount = securityContext.getRoles().size();
//  }
  
  public SecuritySystem(String securitySystemName, Optional<ISecurityContext> securityContext, List<String> appNames)
  {
    
    this.securitySystemName = securitySystemName;
    securitySystemProvider = IConfiguration.get().get("SecuritySystems." + securitySystemName + ".Provider")
            .orElseGet(() -> securityContext.map(c -> c.getExternalSecuritySystemProvider().getProviderName()).orElse(""));
    id = securityContext.map(c -> c.getId()).orElse(0L);
    this.appNames = appNames;
    this.usersCount = securityContext.map(c -> c.getUsers().size() -1).orElse(0);
    this.rolesCount = securityContext.map(c -> c.getRoles().size()).orElse(0);
  }

  public String getSecuritySystemProvider()
  {
    return securitySystemProvider;
  }

  public void setSecuritySystemProvider(String securitySystemProvider)
  {
    this.securitySystemProvider = securitySystemProvider;
  }

  public String getSecuritySystemName()
  {
    return securitySystemName;
  }

  public void setSecuritySystemName(String securitySystemName)
  {
    this.securitySystemName = securitySystemName;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }
  
  public List<String> getAppNames()
  {
    return appNames;
  }

//  public String getAppName()
//  {
//    return appNames.stream().findFirst().orElse("");
//  }

//  public void setAppName(String appName)
//  {
//    this.appName = appName;
//  }

  public int getUsersCount()
  {
    return usersCount;
  }

  public int getRolesCount()
  {
    return rolesCount;
  }
}
