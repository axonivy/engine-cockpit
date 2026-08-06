package ch.ivyteam.enginecockpit.services.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.rest.client.RestClients;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class RestClientsBean implements Serializable {

  private List<RestClientDto> restClients;
  private List<RestClientDto> filteredRestClients;
  private String filter;

  public RestClientsBean() {
    reloadRestClients();
  }

  public void reloadRestClients() {
    var app = ManagerBean.instance().getSelectedApplication();
    if (app == null) {
      restClients = new ArrayList<>();
      return;
    }
    restClients = RestClients.of(app)
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
