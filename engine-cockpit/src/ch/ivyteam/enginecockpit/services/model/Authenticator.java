package ch.ivyteam.enginecockpit.services.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.xml.bind.DatatypeConverter;

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
    return "BASIC " + DatatypeConverter.printBase64Binary(token.getBytes(StandardCharsets.UTF_8));
  }
}
