package ch.ivyteam.enginecockpit.services.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.rest.client.RestClients;

@ManagedBean
@ViewScoped
public class RestClientsBean {

  private List<RestClientDto> restClients;
  private List<RestClientDto> filteredRestClients;
  private String filter;

  private final ManagerBean managerBean;

  public RestClientsBean() {
    managerBean = ManagerBean.instance();
    reloadRestClients();
  }

  public void reloadRestClients() {
    restClients = RestClients.of(managerBean.getSelectedIApplication())
        .all().stream()
        .map(RestClientDto::new)
        .collect(Collectors.toList());
  }

  public List<RestClientDto> getRestClients() {
    return restClients;
  }

  public List<RestClientDto> getFilteredRestClients() {
    return filteredRestClients;
  }

  public void setFilteredRestClients(List<RestClientDto> filteredRestClients) {
    this.filteredRestClients = filteredRestClients;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

}
