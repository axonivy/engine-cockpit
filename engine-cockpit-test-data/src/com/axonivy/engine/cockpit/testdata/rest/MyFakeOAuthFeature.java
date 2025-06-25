package com.axonivy.engine.cockpit.testdata.rest;

import java.io.IOException;

import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import ch.ivyteam.ivy.environment.Ivy;

public class MyFakeOAuthFeature implements Feature {

  @Override
  public boolean configure(FeatureContext context) {
    context.register(BearerFilter.class, Priorities.AUTHORIZATION);
    return true;
  }

  private static class BearerFilter implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
      Ivy.log().debug("Setting the 'Authentication' request header to 'Bearer ******");
      requestContext.getHeaders().add("Authentication", "Bearer 1234MyCoolJWTtoken");
    }
  }

}
