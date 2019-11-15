package ch.ivyteam.enginecockpit.configwizard;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;

@SuppressWarnings("restriction")
@ManagedBean
@SessionScoped
public class WizardBean
{
  private STEPS activeStep;
  private List<User> admins;
  private User editAdmin;
  
  public WizardBean()
  {
    activeStep = STEPS.LICENCE;
    admins = initAdmins();
  }
  
  private static List<User> initAdmins()
  {
    return IConfiguration.get().getNames("Administrators", "Password").stream().map(admin -> {
      User user = new User();
      user.setName(admin);
      user.setEmail(IConfiguration.get().getOrDefault("Administratos." + admin + ".Email"));
      user.setPassword(IConfiguration.get().getOrDefault("Administratos." + admin + ".Password"));
      return user;
    }).collect(Collectors.toList());
  }

  public List<User> getAdmins()
  {
    return admins;
  }
  
  public void setAdmin(User admin)
  {
    Ivy.log().info(admin);
    this.editAdmin = admin;
  }
  
  public void removeAdmin(User admin)
  {
    admins.remove(admin);
  }
  
  public void addAdmin()
  {
    editAdmin = new User();
  }
  
  public User getAdmin()
  {
    return editAdmin;
  }
  
  public void saveAdmin()
  {
    if (!admins.contains(editAdmin))
    {
      admins.add(editAdmin);
    }
    Ivy.log().info(admins);
  }
  
  public int getActiveStep()
  {
    return activeStep.value;
  }
  
  public void nextStep()
  {
    activeStep.value += 1;
    if (activeStep.value >= STEPS.values().length)
    {
      activeStep.value = 0;
    }
  }
  
  public void prevStep()
  {
    activeStep.value -= 1;
    if (activeStep.value < STEPS.values().length)
    {
      activeStep.value = 0;
    }
  }

  public static enum STEPS {
    LICENCE (0),
    ADMINS (1),
    SYSTEMDB (2);
    
    private int value;
    
    STEPS(int value)
    {
      this.value = value;
    }
    
    public static STEPS valueOf(int value)
    {
      STEPS[] values = STEPS.values();
      for (int pos = 0; pos < values .length; pos++)
      {
        if (values[pos].value == value)
        {
          return values[pos];
        }
      }
      return null;
    }
  }
}
