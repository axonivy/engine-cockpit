package ch.ivyteam.enginecockpit.configwizard;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class AdministratorHelper
{
  private List<User> admins;
  private User editAdmin;
  
  public AdministratorHelper()
  {
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
  }
}
