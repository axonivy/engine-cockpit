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
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.licence.LicenceEventManager;
import ch.ivyteam.licence.SystemLicence;

@SuppressWarnings("restriction")
@ManagedBean
@RequestScoped
public class LicenceBean extends StepStatus
{
  private static final String LICENCEE_ORGANISATION = "licencee.organisation";
  private static final String LICENCE_TYPE = "licence.type";
  private static final String LICENCE_VALID_UNTIL = "licence.valid.until";
  private static final String USER_LIMIT = "server.users.limit";
  private static final String SESSION_LIMIT = "server.sessions.limit";
  
  @Inject
  private ISecurityManager securityManager;
  
  private String users;
  private String sessions;
  private List<LicenceMessage> unconfirmedLicenceEvents;
  
  public LicenceBean()
  {
    DiCore.getGlobalInjector().injectMembers(this);
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
    if (users == null)
    { // lazy because calculation can be expensive with many users
      users = calculateUsers();
    }
    return users;
  }
  
  public String getSessions()
  {
    if (sessions == null)
    { // lazy because calculation can be expensive with many sessions
      sessions = calculateSessions();
    }
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
  
  @Override
  public boolean isStepOk()
  {
    return !(isDemo() || (isInstalled() && !isValid()));
  }
  
  @Override
  public String getStepWarningMessage()
  {
    return isDemo() ? "Please upload a valid licence." : getProblemMessage();
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
  
  public List<UserSession> getActiveSessions()
  {
    return securityManager.getSessions().stream()
            .filter(s -> !s.isSessionUserUnknown())
            .filter(s -> !s.isSessionUserSystemUser())
            .map(s -> new UserSession(s))
            .distinct()
            .collect(Collectors.toList());
  }
  
  public void killSession(String session)
  {
    securityManager.getSessions().stream()
            .filter(s -> StringUtils.equals(s.getSessionUserName(), session))
            .forEach(s -> s.logoutSessionUser());
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
    return ISession.current().getSessionUserName();
  }
  
  public class UserSession
  {
    private String name;
    private int count;

    public UserSession(ISession session)
    {
      this.name = session.getSessionUserName();
      this.count = session.getMySessions().size();
    }
    
    public String getName()
    {
      return name;
    }
    
    public int getCount()
    {
      return count;
    }
    
    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }
      if (obj == null || obj.getClass() != this.getClass())
      {
        return false;
      }
      UserSession other = (UserSession) obj;
      return other.name.equals(other.name);
    }
    
    @Override
    public int hashCode()
    {
      return name.hashCode();
    }
  }
}
