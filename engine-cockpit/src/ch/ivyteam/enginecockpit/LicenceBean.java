package ch.ivyteam.enginecockpit;

import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import ch.ivyteam.enginecockpit.util.LicenceUtil;
import ch.ivyteam.licence.SignedLicence;

@ManagedBean
@ApplicationScoped
public class LicenceBean
{
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
    return getValueFromProperty("licencee.organisation");
  }

  public String getLicenceType()
  {
    return getValueFromProperty("licence.type");
  }

  public String getExpiryDate()
  {
    String expiryDate = getValueFromProperty("licence.valid.until");
    return expiryDate.equals("") ? "Never" : expiryDate;
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
