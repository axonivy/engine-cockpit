package ch.ivyteam.enginecockpit.setupwizard;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.environment.Ivy;

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
    Ivy.log().info(activeStep.value);
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
    WEBSERVER (2),
    SYSTEMDB (3);
    
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
