package ch.ivyteam.enginecockpit;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.server.restricted.EngineMode;

@ManagedBean
@SessionScoped
@SuppressWarnings("restriction")
public class LoginBean
{
  private String userName;
  private String password;
  private String originalUrl;

  public void checkLogin()
  {
    if (ISession.get().isSessionUserUnknown())
    {
      originalUrl = evalOriginalUrl();
      loginDefaultAdminOrRedirect();
    }
    FacesContext context = FacesContext.getCurrentInstance();
    context.getApplication().evaluateExpressionGet(context, "#{restartBean}", RestartBean.class).reset();
  }
  
  public void loginDefaultAdminOrRedirect()
  {
    if (EngineMode.is(EngineMode.DEMO))
    {
      if(ISession.get().loginSessionUser("admin", "admin"))
      {
        return;
      }
    }
    redirect();
  }

  public void login()
  {
    if (ISession.get().loginSessionUser(userName, password))
    {
      redirect(StringUtils.isNotBlank(originalUrl) ? originalUrl : "dashboard.xhtml");
      return;
    }
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed", "Login failed"));
  }

  public void logout()
  {
    ISession.get().logoutSessionUser();
    redirect();
  }
  
  public void redirectToLoginPage()
  {
    originalUrl = evalOriginalUrl();
    redirect();
  }
  
  private void redirect()
  {
    redirect("login.xhtml");
  }
  
  private void redirect(String url)
  {
    try
    {
      FacesContext context = FacesContext.getCurrentInstance();
      context.getExternalContext().redirect(url);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Could not send redirect", e);
    }
  }
  
  private static String evalOriginalUrl()
  {
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    return request.getRequestURI();
  }

  public String getSessionUserName()
  {
    return ISession.get().getSessionUserName();
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }
}
