package ch.ivyteam.enginecockpit.dashboad;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.LicenceMessage;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.licence.LicenceEventManager;
import ch.ivyteam.licence.SignedLicence;

@ManagedBean
@RequestScoped
public class LicenceBean
{
  private static final String LICENCEE_ORGANISATION = "licencee.organisation";
  private static final String LICENCE_TYPE = "licence.type";
  private static final String LICENCE_VALID_UNTIL = "licence.valid.until";
  private static final String USER_LIMIT = "server.users.limit";
  private static final String SESSION_LIMIT = "server.sessions.limit";
  
  @Inject
  private ISecurityManager securityManager;
  
  private final String users;
  private final String sessions;
  private List<LicenceMessage> unconfirmedLicenceEvents;
  
  public LicenceBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
    users = calculateSessions();
    sessions = calculateUsers();
    reloadLicenceMessages();
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
    return StringUtils.equals(expiryDate, "") ? "Never" : expiryDate;
  }
  
  public boolean showRenewLicFeature()
  {
    try
    {
      return !isDemo() && SignedLicence.getValidUntil() != null;
    }
    catch (ParseException ex)
    {
      return false;
    }
  }

  public boolean showExpiryWarning()
  {
    Calendar now = Calendar.getInstance();
    now.add(Calendar.MONTH, 3);
    try
    {
      return SignedLicence.isExpired(now.getTime());
    }
    catch (ParseException e)
    {
      return false;
    }
  }
  
  public String getUsers()
  {
    return users;
  }
  
  public String getSessions()
  {
    return sessions;
  }

  public boolean isCluster()
  {
    return SignedLicence.isEnterprise();
  }

  public boolean isDemo()
  {
    return SignedLicence.isDemo();
  }
  
  public List<LicenceMessage> getLicenceEvents()
  {
    return unconfirmedLicenceEvents;
  }
  
  public int getLicenceEventCount()
  {
    int size = unconfirmedLicenceEvents.size();
    return size < 100 ? size : 99;
  }
  
  public void confirmLicenceEvent(LicenceMessage event)
  {
    event.confirm(getSessionUsername());
    reloadLicenceMessages();
  }
  
  public void confirmAllLicenceEvents()
  {
    LicenceEventManager.getInstance().confirmAll(getSessionUsername());
    reloadLicenceMessages();
  }
  
  private String calculateSessions()
  {
    int licensedSessions = securityManager.getLicensedSessions();
    String sessionLimit = getValueFromProperty(SESSION_LIMIT);
    if (sessionLimit != null && NumberUtils.isParsable(sessionLimit) && Long.parseLong(sessionLimit) > 0)
    {
      return licensedSessions + " / " + sessionLimit; 
    }
    return String.valueOf(licensedSessions);
  }
  
  private String calculateUsers()
  {
    long licensedUsers = securityManager.countLicensedUsers();
    String usersLimit = getValueFromProperty(USER_LIMIT);
    if (usersLimit != null && NumberUtils.isParsable(usersLimit) && Long.parseLong(usersLimit) > 0)
    {
      return licensedUsers + " / " + usersLimit;
    }
    return String.valueOf(licensedUsers);
  }
  
  private void reloadLicenceMessages()
  {
    unconfirmedLicenceEvents = LicenceEventManager.getInstance().getUnconfirmedLicenceEvents().stream()
            .map(event -> new LicenceMessage(event)).collect(Collectors.toList());
  }

  private String getSessionUsername()
  {
    return ISession.get().getSessionUserName();
  }
}
