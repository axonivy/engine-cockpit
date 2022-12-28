package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.services.model.Webservice;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.webservice.client.WebServiceClients;

@ManagedBean
@ViewScoped
public class WebserviceBean {
  private List<Webservice> webservices;
  private List<Webservice> filteredWebservices;
  private String filter;

  private ManagerBean managerBean;

  public WebserviceBean() {
    managerBean = ManagerBean.instance();
    reloadWebservices();
  }

  public void reloadWebservices() {
    webservices = WebServiceClients
            .of(managerBean.getSelectedIApplication())
            .all().stream()
            .map(web -> new Webservice(web))
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
