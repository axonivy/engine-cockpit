package ch.ivyteam.enginecockpit;

import java.util.ArrayList;
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
	private List<User> users;
	
	private ApplicationBean applicationBean;
	
	public UserBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
		reloadUsers();
	}
	
	public void reloadUsers() {
		filteredUsers = null;
		IApplication app = applicationBean.getSelectedIApplication();
		users = getUsersOfApp(app);
	}
	
	public List<User> getUsers() {
		return users;
	}

	private List<User> getUsersOfApp(IApplication app) {
		//TODO: remove
		if (app == null) {
			List<User> users = new ArrayList<User>();
			User user1 = new User();
			user1.setName("testUser1");
			User user2 = new User();
			user2.setName("testUser2");
			users.add(user1);
			users.add(user2);
			return users;
		}
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
