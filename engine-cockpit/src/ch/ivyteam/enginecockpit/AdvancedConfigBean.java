package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.configuration.restricted.Property;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class AdvancedConfigBean {
	private List<Property> configs;
	private List<Property> filteredConfigs;
	private Property selectedConfig;
	
	public AdvancedConfigBean() {
		configs = IConfiguration.get().getProperties();
	}
	
	public List<Property> getConfigs() {
		return configs;
	}
	
	public Property getSelectedConfig() {
        return selectedConfig;
    }
 
    public void setSelectedConfig(Property selectedConfig) {
        this.selectedConfig = selectedConfig;
    }
    
    public List<Property> getFilteredConfigs() {
        return filteredConfigs;
    }
    
    public void setFilteredConfigs(List<Property> filteredConfigs) {
        this.filteredConfigs = filteredConfigs;
    }
    
    public void onRowSelect(SelectEvent event) {
        FacesMessage msg = new FacesMessage("Config Selected", ((Property) event.getObject()).getKey());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
