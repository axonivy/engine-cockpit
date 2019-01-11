package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;


import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.SessionInfo;

@ManagedBean
@ViewScoped
public class UserBean {
	private List<User> filteredUsers;
	
	private ApplicationBean applicationBean;
	
	public UserBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
	}
	
	public List<User> getUsersForAppId(long id) {
		IApplication app = applicationBean.getIApplication(id);
		return getUsersOfApp(app);
	}
	
	public List<User> getUsers() {
		IApplication app = applicationBean.getSelectedIApplication();
		return getUsersOfApp(app);
	}

	private List<User> getUsersOfApp(IApplication app) {
		List<User> users = app.getSecurityContext().getUsers().stream()
				.map(user -> new User(user))
				.collect(Collectors.toList());
		checkIfUserIsLoggedIn(app, users);
		return users;
	}
	
	private void checkIfUserIsLoggedIn(IApplication app, List<User> users) {
		for (SessionInfo session : app.getSecurityContext().getClusterSessionsSnapshot().getSessionInfos()) {
			String sessionUser = session.getSessionUserName();
			Optional<User> user = users.stream()
					.filter(u -> u.getName().equals(sessionUser))
					.findAny();
			if (user.isPresent()) {
				user.get().setLoggedIn(true);
			}
		}
	}
    
    public List<User> getFilteredUsers() {
        return filteredUsers;
    }
    
    public void setFilteredUsers(List<User> filteredUsers) {
        this.filteredUsers = filteredUsers;
    }
    
}
