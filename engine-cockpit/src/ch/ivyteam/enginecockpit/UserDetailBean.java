package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class UserDetailBean {
	private String userName;
	private User user;
	
	private ApplicationBean applicationBean;
	
	public UserDetailBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
		this.user = new User(applicationBean.getSelectedIApplication().getSecurityContext().findUser(userName));
	}
	
	public User getUser() {
		return user;
	}
	
	public String deleteSelectedUser() {
    	Ivy.log().info("delete user");
    	applicationBean.getSelectedIApplication().getSecurityContext().deleteUser(userName);
//    	applicationBean.getSelectedIApplication().getSecurityContext().createUser(userName, fullUserName, password, null, eMailAddress, null)
    	return "users.xhtml";
    }
}
