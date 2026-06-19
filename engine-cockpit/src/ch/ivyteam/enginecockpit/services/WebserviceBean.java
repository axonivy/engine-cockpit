package ch.ivyteam.enginecockpit.services;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.services.model.Webservice;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.webservice.client.WebServiceClients;

@Named
@ViewScoped
public class WebserviceBean implements Serializable {
  private List<Webservice> webservices;
  private List<Webservice> filteredWebservices;
  private String filter;

  private final ManagerBean managerBean;

  public WebserviceBean() {
    managerBean = ManagerBean.instance();
    reloadWebservices();
  }

  public void reloadWebservices() {
    webservices = WebServiceClients
        .of(managerBean.getSelectedIApplication())
        .all().stream()
        .map(Webservice::new)
        .collect(Collectors.toList());
  }

  public List<Webservice> getWebservices() {
    return webservices;
  }

  public List<Webservice> getFilteredWebservices() {
    return filteredWebservices;
  }

  public void setFilteredWebservices(List<Webservice> filteredWebservices) {
    this.filteredWebservices = filteredWebservices;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

}
