package ch.ivyteam.enginecockpit.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.services.model.RestClient;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.restricted.rest.RestClientDao;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class RestClientsBean
{
  private List<RestClient> restClients;
  private List<RestClient> filteredRestClients;
  private String filter;
  
  private ManagerBean managerBean;
  
  public RestClientsBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadRestClients();
  }

  public void reloadRestClients()
  {
    var restClientDao = RestClientDao.forApp(managerBean.getSelectedIApplication());
    restClients = restClientDao.getAll(managerBean.getSelectedIEnvironment()).stream()
            .map(rest -> new RestClient(rest))
            .collect(Collectors.toList());
  }
  
  public List<RestClient> getRestClients()
  {
    return restClients;
  }
  
  public List<RestClient> getFilteredRestClients()
  {
    return filteredRestClients;
  }

  public void setFilteredRestClients(List<RestClient> filteredRestClients)
  {
    this.filteredRestClients = filteredRestClients;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
  
  
}
