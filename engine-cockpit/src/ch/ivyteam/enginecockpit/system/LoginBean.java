package ch.ivyteam.enginecockpit.system;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.server.restricted.EngineMode;

@ManagedBean
@SessionScoped
@SuppressWarnings("restriction")
public class LoginBean {
  private String userName;
  private String password;
  private String originalUrl;

  public void checkLogin() {
    if (ISession.current().isSessionUserUnknown()) {
      originalUrl = evalOriginalUrl();
      loginDefaultAdminOrRedirect();
    }
  }

  public void loginDefaultAdminOrRedirect() {
    if (EngineMode.isAnyOf(EngineMode.DEMO, EngineMode.DESIGNER_EMBEDDED)) {
      if (ISession.current().loginSessionUser("admin", "admin")) {
        return;
      }
    }
    redirect();
  }

  public void login() {
    if (ISession.current().loginSessionUser(userName, password)) {
      redirect(StringUtils.isNotBlank(originalUrl) ? originalUrl : "dashboard.xhtml");
      return;
    }
    FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login failed", "Login failed"));
  }

  public void logout() {
    ISession.current().logoutSessionUser();
    redirect();
  }

  public void redirectToLoginPage() {
    originalUrl = evalOriginalUrl();
    redirect();
  }

  private void redirect() {
    redirect("login.xhtml");
  }

  private void redirect(String url) {
    try {
      FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    } catch (IOException e) {
      throw new RuntimeException("Could not send redirect", e);
    }
  }

  private static String evalOriginalUrl() {
    var request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    return request.getRequestURI();
  }

  public String getSessionUserName() {
    return ISession.current().getSessionUserName();
  }

  public String getGravatarHash() {
    var session = ISession.current();
    if (session == null) {
      return null;
    }
    var sessionUser = session.getSessionUser();
    if (sessionUser == null) {
      return null;
    }
    return EmailUtil.gravatarHash(sessionUser.getEMailAddress());
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
