package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class UserDetailBean {
	private String userName;
	private User user;
	
	private ApplicationBean applicationBean;
	
	public UserDetailBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
		user = new User();
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
		this.user = new User(getSecurityContext().findUser(userName));
	}
	
	public User getUser() {
		return user;
	}
	
	public void creatNewUser() {
    	getSecurityContext().createUser(user.getName(), user.getFullName(), user.getPassword(), null, user.getEmail(), null);
	}
	
	public void saveUserInfos() {
		Ivy.log().info("save user");
		IUser iUser = getSecurityContext().findUser(userName);
		iUser.setEMailAddress(user.getEmail());
		iUser.setFullName(user.getFullName());
		if (user.getPassword() != "") {
			iUser.setPassword(user.getPassword());
		}
	}
	
	public String deleteSelectedUser() {
    	getSecurityContext().deleteUser(userName);
    	return "users.xhtml";
    }

	private ISecurityContext getSecurityContext() {
		return applicationBean.getSelectedIApplication().getSecurityContext();
	}
}
