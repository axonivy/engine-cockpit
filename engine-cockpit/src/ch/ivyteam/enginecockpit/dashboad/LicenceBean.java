package ch.ivyteam.enginecockpit.dashboad;

import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.util.LicenceUtil;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.licence.SignedLicence;

@ManagedBean
@ApplicationScoped
public class LicenceBean
{
  private static final String LICENCEE_ORGANISATION = "licencee.organisation";
  private static final String LICENCE_TYPE = "licence.type";
  private static final String LICENCE_VALID_UNTIL = "licence.valid.until";
  private static final String USER_LIMIT = "server.users.limit";
  private static final String SESSION_LIMIT = "server.sessions.limit";
  
  @Inject
  private ISecurityManager securityManager;
  
  private final int licensedSessions;
  private final long licensedUsers;
  
  public LicenceBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    licensedSessions = securityManager.getLicensedSessions();
    licensedUsers = securityManager.countLicensedUsers();
  }

  public String getValueFromProperty(String key)
  {
    return getLicenceProperties().getProperty(key);
  }

  public Properties getLicenceProperties()
  {
    return SignedLicence.getLicenceParameters();
  }

  public String getOrganisation()
  {
    return getValueFromProperty(LICENCEE_ORGANISATION);
  }

  public String getLicenceType()
  {
    return getValueFromProperty(LICENCE_TYPE);
  }

  public String getExpiryDate()
  {
    String expiryDate = getValueFromProperty(LICENCE_VALID_UNTIL);
    return expiryDate.equals("") ? "Never" : expiryDate;
  }
  
  public String getUsers()
  {
    String usersLimit = getValueFromProperty(USER_LIMIT);
    if (usersLimit != null && NumberUtils.isParsable(usersLimit) && Long.parseLong(usersLimit) > 0)
    {
      return licensedUsers + " / " + usersLimit;
    }
    return String.valueOf(licensedUsers);
  }
  
  public String getSessions()
  {
    String sessionLimit = getValueFromProperty(SESSION_LIMIT);
    if (sessionLimit != null && NumberUtils.isParsable(sessionLimit) && Long.parseLong(sessionLimit) > 0)
    {
      return licensedSessions + " / " + sessionLimit; 
    }
    return String.valueOf(licensedSessions);
  }

  public boolean isCluster()
  {
    return LicenceUtil.isCluster();
  }

  public boolean isDemo()
  {
    return LicenceUtil.isDemo();
  }
}
