package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.application.IApplication;

public class Application {
	private String name;
	private long id;
	
	public Application(IApplication app) {
		this(app.getName(), app.getId());
	}
	
	public Application(String name, long id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
