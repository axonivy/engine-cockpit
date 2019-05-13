package ch.ivyteam.enginecockpit.services;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.RestClient;
import ch.ivyteam.ivy.application.restricted.rest.RestClientDao;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean
{
  private RestClient restClient;
  private String databaseName;
  
  private ManagerBean managerBean;
  private RestClientDao restClientDao;
  
  public RestClientDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    restClientDao = RestClientDao.forApp(managerBean.getSelectedIApplication());
  }
  
  public String getRestClientName()
  {
    return databaseName;
  }
  
  public void setRestClientName(String restClientName)
  {
    this.databaseName = restClientName;
    restClient = new RestClient(restClientDao.findByName(restClientName));
  }
  
  public RestClient getRestClient()
  {
    return restClient;
  }
    
}
