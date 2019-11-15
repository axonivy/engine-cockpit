package ch.ivyteam.enginecockpit.configwizard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class WizardBean
{
  private STEPS activeStep;
  private AdministratorHelper admins;
  private SystemDatabaseHelper systemDb;
  
  public WizardBean()
  {
    activeStep = STEPS.LICENCE;
    admins = new AdministratorHelper();
    systemDb = new SystemDatabaseHelper();
  }
  
  public AdministratorHelper getAdmins()
  {
    return admins;
  }
  
  public SystemDatabaseHelper getSystemDb()
  {
    return systemDb;
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
    if (activeStep.value < 0)
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
