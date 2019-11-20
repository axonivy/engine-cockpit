package ch.ivyteam.enginecockpit.setupwizard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class WizardBean
{
  private STEPS activeStep;
  
  public WizardBean()
  {
    activeStep = STEPS.LICENCE;
  }
  
  public int getActiveStep()
  {
    return activeStep.value;
  }
  
  public void nextStep()
  {
    activeStep = STEPS.valueOf(activeStep.value + 1);
  }
  
  public void prevStep()
  {
    activeStep = STEPS.valueOf(activeStep.value - 1);
  }

  public static enum STEPS {
    LICENCE (0),
    ADMINS (1),
    WEBSERVER (2),
    SYSTEMDB (3);
    
    private final int value;
    
    STEPS(int value)
    {
      this.value = value;
    }
    
    public static STEPS valueOf(int index)
    {
      if (index >= STEPS.values().length || index < 0)
      {
        return STEPS.LICENCE;
      }
      return values()[index];
    }
  }
}
