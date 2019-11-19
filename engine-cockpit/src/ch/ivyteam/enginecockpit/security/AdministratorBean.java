package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class AdministratorBean
{
  private static final String ADMINS = "Administrators";
  private static final String ADMINS_DOT = ADMINS + ".";
  private static final String PASSWORD = "Password";
  private static final String DOT_PASSWORD = "." + PASSWORD;
  private static final String DOT_EMAIL = ".Email";
  private List<User> admins;
  private User editAdmin;
  
  public AdministratorBean()
  {
    admins = initAdmins();
  }
  
  private static List<User> initAdmins()
  {
    return IConfiguration.get().getNames(ADMINS, PASSWORD).stream().map(admin -> {
      User user = new User();
      user.setName(admin);
      user.setEmail(IConfiguration.get().getOrDefault(ADMINS_DOT + admin + DOT_EMAIL));
      user.setPassword(IConfiguration.get().getOrDefault(ADMINS_DOT + admin + DOT_PASSWORD));
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
    IConfiguration.get().remove(ADMINS_DOT + admin.getName());
    FacesContext.getCurrentInstance().addMessage("",
            new FacesMessage("'" + admin.getName() + "' removed successfully"));
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
    var message = new FacesMessage("'" + editAdmin.getName() + "' modified successfully");
    if (!admins.contains(editAdmin))
    {
      admins.add(editAdmin);
      message = new FacesMessage("'" + editAdmin.getName() + "' added successfully");
    }
    IConfiguration.get().set(ADMINS_DOT + "'" + editAdmin.getName() + "'" + DOT_EMAIL, editAdmin.getEmail());
    IConfiguration.get().set(ADMINS_DOT + "'" + editAdmin.getName() + "'" + DOT_PASSWORD, editAdmin.getPassword());
    FacesContext.getCurrentInstance().addMessage("", message);
  }
}
