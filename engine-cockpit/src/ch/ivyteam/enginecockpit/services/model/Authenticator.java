package ch.ivyteam.enginecockpit.services.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.xml.bind.DatatypeConverter;

public class Authenticator implements ClientRequestFilter {

  private final String user;
  private final String password;

  public Authenticator(String user, String password) {
    this.user = user;
    this.password = password;
  }

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    var headers = requestContext.getHeaders();
    final String basicAuthentication = getBasicAuthentication();
    headers.add("Authorization", basicAuthentication);

  }

  private String getBasicAuthentication() {
    var token = this.user + ":" + this.password;
    try {
      return "BASIC " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException ex) {
      throw new IllegalStateException("Cannot encode with UTF-8", ex);
    }
  }
}
