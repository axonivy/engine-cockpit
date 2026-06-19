package ch.ivyteam.enginecockpit.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.request.EngineUriResolver;

@Named
@ViewScoped
public class WebServerBean implements Serializable {
  private final static List<String> CHECK_SECURITY_HEADERS = List.of("X-Frame-Options", "Referrer-Policy", "Content-Security-Policy", "X-Content-Type-Options", "Strict-Transport-Security");
  private String baseUrl;
  private boolean showRequestHeaders = false;
  private boolean showResponseHeaders = false;

  public WebServerBean() {
    baseUrl = EngineUriResolver.instance().baseUrl().toString();
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<HeaderData> getRequestData() {
    var request = getRequest();
    var requestData = new ArrayList<HeaderData>();
    request.getHeaderNames().asIterator()
        .forEachRemaining(name -> requestData.add(new HeaderData(name, request.getHeader(name))));
    return requestData;
  }

  public List<HeaderData> getResponseData() {
    var response = getResponse();
    var responseData = new ArrayList<HeaderData>();
    response.getHeaderNames().forEach(name -> responseData.add(new HeaderData(name, response.getHeader(name))));
    return responseData;
  }

  public String getMissingSecurityHeaders() {
    var missingHeaders = new ArrayList<String>();
    var headerNames = getResponseData().stream().map(header -> header.name.toLowerCase()).toList();
    CHECK_SECURITY_HEADERS.stream()
        .filter(header -> !headerNames.contains(header.toLowerCase()))
        .forEach(missingHeaders::add);
    return missingHeaders.stream().collect(Collectors.joining(", "));
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

  public void setShowRequestHeaders() {
    showRequestHeaders = true;
  }

  public boolean isShowRequestHeaders() {
    return showRequestHeaders;
  }

  public void setShowResponseHeaders() {
    showResponseHeaders = true;
  }

  public boolean isShowResponseHeaders() {
    return showResponseHeaders;
  }

  public void saveBaseUrl() {
    EngineUriResolver.instance().baseUrl(baseUrl);
    var msg = new FacesMessage(Ivy.cm().co("/webServer/BaseUrlSavedMessage"));
    FacesContext.getCurrentInstance().addMessage("baseUrlSaveGrowl", msg);
  }

  private HttpServletRequest getRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }

  private HttpServletResponse getResponse() {
    return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
  }

  public class HeaderData {
    private final String name;
    private final String value;

    HeaderData(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
  }
}
