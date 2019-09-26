package ch.ivyteam.enginecockpit.util;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ElasticSearchUtil
{
  public static String getVersion(String serverUrl)
  {
    Client client = ClientBuilder.newClient();
    Response response = client.target(serverUrl).request().get();
    String json = response.readEntity(String.class);
    JsonObject jObj = new Gson().fromJson(json, JsonObject.class);
    return jObj.get("version").getAsJsonObject().get("number").toString().replace("\"", "");
  }
}
