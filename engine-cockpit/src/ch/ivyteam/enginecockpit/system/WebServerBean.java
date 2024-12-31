package ch.ivyteam.enginecockpit.system;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ch.ivyteam.ivy.request.EngineUriResolver;

@ManagedBean
@ViewScoped
public class WebServerBean {
  private String baseUrl;
  private boolean showHeaders = false;

  public WebServerBean() {
    baseUrl = EngineUriResolver.instance().baseUrl().toString();
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<RequestData> getRequestData() {
    var request = getRequest();
    var requestData = new ArrayList<RequestData>();
    request.getHeaderNames().asIterator()
        .forEachRemaining(header -> requestData.add(new RequestData(header, request.getHeader(header))));
    return requestData;
  }

  public String getExternalUrl() {
    return EngineUriResolver.instance().external().toString();
  }

  public String getEvaluatedUrl() {
    return getExternalUrl() + getRequest().getRequestURI();
  }

  public String getRemoteAddr() {
    return getRequest().getRemoteAddr();
  }

  public void setShowHeaders() {
    showHeaders = true;
  }

  public boolean isShowHeaders() {
    return showHeaders;
  }

  public void saveBaseUrl() {
    EngineUriResolver.instance().baseUrl(baseUrl);
    var msg = new FacesMessage("Base Url saved");
    FacesContext.getCurrentInstance().addMessage("baseUrlSaveGrowl", msg);
  }

  private HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }

  public class RequestData {
    private final String data;
    private final String value;

    RequestData(String data, String value) {
      this.data = data;
      this.value = value;
    }

    public String getData() {
      return data;
    }

    public String getValue() {
      return value;
    }
  }
}
