package ch.ivyteam.enginecockpit.services;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Webservice;

@ManagedBean
@ViewScoped
public class WebserviceDetailBean
{
  private Webservice webservice;
  private String webserviceId;
  
  private ManagerBean managerBean;
  
  public WebserviceDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getWebserviceId()
  {
    return webserviceId;
  }
  
  public void setWebserviceId(String webserviceId)
  {
    this.webserviceId = webserviceId;
    webservice = new Webservice(managerBean.getSelectedIApplication().findWebService(webserviceId));
  }
  
  public Webservice getWebservice()
  {
    return webservice;
  }
    
}
