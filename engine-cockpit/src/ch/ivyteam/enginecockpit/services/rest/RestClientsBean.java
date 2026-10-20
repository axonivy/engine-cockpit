package ch.ivyteam.enginecockpit.services.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.services.model.RestClientDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.rest.client.RestClients;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class RestClientsBean implements Serializable {

  @Inject
  private ManagerBean managerBean;

  private List<RestClientDto> restClients;
  private List<RestClientDto> filteredRestClients;
  private String filter;

  @PostConstruct
  public void init() {
    reloadRestClients();
  }

  public void reloadRestClients() {
    var app = managerBean.getSelectedApplication();
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
