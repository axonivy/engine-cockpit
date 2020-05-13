package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.Optional;

import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityContext;

@SuppressWarnings("restriction")
public class SecuritySystem
{

  private String securitySystemProvider;
  private String securitySystemName;
  private long id;
  private List<String> appNames;
  private long usersCount;
  private int rolesCount;
  
  public SecuritySystem(String securitySystemName, Optional<ISecurityContext> securityContext, List<String> appNames)
  {
    this.securitySystemName = securitySystemName;
    securitySystemProvider = IConfiguration.get().get("SecuritySystems." + securitySystemName + ".Provider")
            .orElseGet(() -> securityContext.map(c -> c.getExternalSecuritySystemName()).orElse(SecuritySystemConfig.IVY_SECURITY_SYSTEM));
    id = securityContext.map(c -> c.getId()).orElse(0L);

    this.appNames = appNames;
    this.usersCount = securityContext.map(c -> countUser(c)).orElse(0l);
    this.rolesCount = securityContext.map(c -> c.getRoles().size()).orElse(0);
  }

  private long countUser(ISecurityContext securityContext)
  {
    return securityContext.users().count();
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

  public long getUsersCount()
  {
    return usersCount;
  }

  public int getRolesCount()
  {
    return rolesCount;
  }
}
