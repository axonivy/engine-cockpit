package ch.ivyteam.enginecockpit.dashboad;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.licence.LicenceEventManager;
import ch.ivyteam.licence.SystemLicence;

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
    users = calculateUsers();
    sessions = calculateSessions();
    reloadLicenceMessages();
  }

  public String getValueFromProperty(String key)
  {
    return SystemLicence.getParam(key);
  }

  public Properties getLicenceProperties()
  {
    return SystemLicence.getParams();
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
    return StringUtils.isBlank(expiryDate) ? "Never" : expiryDate;
  }
  
  public boolean showRenewLicFeature()
  {
    try
    {
      return SystemLicence.isInstalled() && SystemLicence.getValidUntil() != null;
    }
    catch (DateTimeParseException ex)
    {
      return false;
    }
  }

  public boolean showExpiryWarning()
  {
	LocalDate inThreeMonth = LocalDate
	    .now()
	    .plus(3, ChronoUnit.MONTHS);
    try
    {
      return SystemLicence.isExpiredAt(inThreeMonth);
    }
    catch (DateTimeParseException e)
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
    return SystemLicence.isEnterprise();
  }

  public boolean isDemo()
  {
    return SystemLicence.isDemo();
  }
  
  public boolean isInstalled()
  {
    return SystemLicence.isInstalled();
  }
  
  public boolean isValid()
  {
    return SystemLicence.isValid();
  }
  
  public boolean isExpired()
  {
    return SystemLicence.isExpired();
  }
  
  public String getProblemMessage()
  {
    String maintenanceMode = EngineMode.is(EngineMode.MAINTENANCE) ? " Your engine runs in maintenance mode." : "";
    if (isExpired())
    {
      return "Your licence has expired."+maintenanceMode;
    }
    if (!isValid())
    {
      return "Invalid licence installed."+maintenanceMode;
    }
    return "";
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
    return licensedSessions + " / Unlimited";
  }
  
  private String calculateUsers()
  {
    long licensedUsers = securityManager.countLicensedUsers();
    String usersLimit = getValueFromProperty(USER_LIMIT);
    if (usersLimit != null && NumberUtils.isParsable(usersLimit) && Long.parseLong(usersLimit) > 0)
    {
      return licensedUsers + " / " + usersLimit;
    }
    return licensedUsers + " / Unlimited";
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
