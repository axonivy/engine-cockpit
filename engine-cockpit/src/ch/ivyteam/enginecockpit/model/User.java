package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.IUser;

public class User {
	private String name;
	private String fullName;
	private String email;
	private String password;

	private boolean loggedIn;
	private long id;

	public User() {

	}

	public User(IUser user) {
		this.name = user.getName();
		this.fullName = user.getFullName();
		this.email = user.getEMailAddress();
		this.loggedIn = false;
		this.id = user.getId();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return name;
	}

}
