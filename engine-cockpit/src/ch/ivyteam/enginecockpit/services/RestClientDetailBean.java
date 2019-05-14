package ch.ivyteam.enginecockpit.services;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.RestClient;
import ch.ivyteam.ivy.application.restricted.rest.IRestClient;
import ch.ivyteam.ivy.application.restricted.rest.RestClientDao;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class RestClientDetailBean
{
  private RestClient restClient;
  private String restClientName;
  
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
    return restClientName;
  }
  
  public void setRestClientName(String restClientName)
  {
    this.restClientName = restClientName;
    IRestClient iRestClient = restClientDao.findByName(restClientName, managerBean.getSelectedIEnvironment());
    if (iRestClient == null)
    {
      iRestClient = restClientDao.findByName(restClientName);
    }
    restClient = new RestClient(iRestClient);
  }
  
  public RestClient getRestClient()
  {
    return restClient;
  }
    
}
