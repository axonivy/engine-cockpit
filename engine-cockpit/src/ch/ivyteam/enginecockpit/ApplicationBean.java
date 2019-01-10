package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.TabChangeEvent;

import com.google.inject.Inject;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;

@ManagedBean(name = "applicationBean")
@SessionScoped
public class ApplicationBean {

	private int selectedApplicationIndex;
	private List<Application> applications;
	
	@Inject
	IApplicationConfigurationManager manager;
	
	public ApplicationBean() {
		DiCore.getGlobalInjector().injectMembers(this);
		
		applications = manager.getApplications().stream()
				.map(app -> new Application(app))
				.collect(Collectors.toList());
	}
	
	public List<Application> getApplications() {
		return applications;
	}
	
	public int getSelectedApplicationIndex() {
		return selectedApplicationIndex;
	}
	
	public void setSelectedApplicationIndex(int index) {
		selectedApplicationIndex = index;
	}
	
	public void updateSelectedApplication(TabChangeEvent event) {
    	setSelectedApplicationIndex(0);
    	for (Application app : applications) {
    		if (app.getName().equals(event.getTab().getTitle())) {
    			setSelectedApplicationIndex(applications.indexOf(app));
    		}
    	}
    }
	
	public Application getSelectedApplication() {
    	return applications.get(selectedApplicationIndex);
    }
	
	public IApplication getSelectedIApplication() {
		return manager.getApplication(getSelectedApplication().getId());
	}
	
	public IApplication getIApplication(long id) {
		return manager.getApplication(id);
	}
}
