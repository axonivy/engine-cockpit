package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.SessionInfo;

@ManagedBean
@ViewScoped
public class UserBean
{
  private List<User> filteredUsers;
  private List<User> users;

  private ApplicationBean applicationBean;

  public UserBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    reloadUsers();
  }

  public void reloadUsers()
  {
    filteredUsers = null;
    IApplication app = applicationBean.getSelectedIApplication();
    users = getUsersOfApp(app);
  }

  public List<User> getUsers()
  {
    return users;
  }

  private List<User> getUsersOfApp(IApplication app)
  {
    List<User> appUsers = app.getSecurityContext().getUsers().stream()
            .map(user -> new User(user))
            .collect(Collectors.toList());
    checkIfUserIsLoggedIn(app, appUsers);
    return appUsers;
  }

  private void checkIfUserIsLoggedIn(IApplication app, List<User> appUsers)
  {
    for (SessionInfo session : app.getSecurityContext().getClusterSessionsSnapshot().getSessionInfos())
    {
      String sessionUser = session.getSessionUserName();
      Optional<User> user = appUsers.stream().filter(u -> u.getName().equals(sessionUser)).findAny();
      if (user.isPresent())
      {
        user.get().setLoggedIn(true);
      }
    }
  }

  public List<User> getFilteredUsers()
  {
    return filteredUsers;
  }

  public void setFilteredUsers(List<User> filteredUsers)
  {
    this.filteredUsers = filteredUsers;
  }
  
  public List<User> searchUser(String query)
  {
    List<User> search = users.stream().filter(u -> StringUtils.startsWithIgnoreCase(u.getName(), query)).limit(10).collect(Collectors.toList());
    return search;
  }

}
