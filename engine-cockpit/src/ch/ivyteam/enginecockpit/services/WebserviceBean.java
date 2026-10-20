package ch.ivyteam.enginecockpit.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.services.model.Webservice;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.webservice.client.WebServiceClients;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class WebserviceBean implements Serializable {

  @Inject
  private ManagerBean managerBean;

  private List<Webservice> webservices;
  private List<Webservice> filteredWebservices;
  private String filter;

  @PostConstruct
  public void init() {
    reloadWebservices();
  }

  public void reloadWebservices() {
    var app = managerBean.getSelectedApplication();
    if (app == null) {
      webservices = new ArrayList<>();
      return;
    }
    webservices = WebServiceClients.of(app).all().stream()
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
