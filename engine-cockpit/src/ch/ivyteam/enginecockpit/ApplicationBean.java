package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

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
		//TODO: remove
		applications.add(new Application("test", 0));
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
		//TODO: remove
		if (getSelectedApplication().getId() == 0) {
			return null;
		}
		return manager.getApplication(getSelectedApplication().getId());
	}
	
	public IApplication getIApplication(long id) {
		return manager.getApplication(id);
	}
	
	public Locale getDefaultEmailLanguageForSelectedApp() {
		return getSelectedIApplication().getDefaultEMailLanguage();
	}
	
	public List<SelectItem> getSupportedLanguages() {
		return manager.getLanguages().stream()
				.map(l -> new SelectItem(l.getLocale().getLanguage(), l.getLocale().getDisplayLanguage()))
				.collect(Collectors.toList());
	}
}
