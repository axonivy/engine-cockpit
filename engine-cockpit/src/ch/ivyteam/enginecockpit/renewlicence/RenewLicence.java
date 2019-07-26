package ch.ivyteam.enginecockpit.renewlicence;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.licence.SignedLicence;

@ManagedBean
@SessionScoped
public class RenewLicence
{
  public void send(String mailTo) throws IOException
  {
      FormDataMultiPart multipart = createMultipart(SignedLicence.getLicenceContent());
      showResultMessage(executeCall(mailTo, multipart));
  }

  private Response executeCall(String mailTo, FormDataMultiPart multipart)
  {
    Response response = null;
    if (mailTo.equals("webTest@renewLicence.axonivy.test")) 
    {
      response = Response.status(301).entity("This is for testing").build();
    }
    else 
    {
      try
      {
        response = createClient().target(getUri("api")).request()
                .header("X-Requested-By", "ivy")
                .header("MIME-Version", "1.0")
                .header("mailTo", mailTo)
                .put(Entity.entity(multipart, multipart.getMediaType()));
      }
      catch (ResponseProcessingException ex)
      {
        response = Response.status(400).entity("There was problem with requesting response ").build();
      }
    }
    return response;
  }

  private FormDataMultiPart createMultipart(String licContent) throws IOException
  {
    FormDataMultiPart multipart;
    try (FormDataMultiPart formDataMultiPart = new FormDataMultiPart())
    {
      FormDataBodyPart filePart = new FormDataBodyPart("oldLicense", licContent);
      multipart = (FormDataMultiPart) formDataMultiPart.field("oldLicense", licContent,
              MediaType.MULTIPART_FORM_DATA_TYPE).bodyPart(filePart);
      multipart.setMediaType(Boundary.addBoundary(MediaType.MULTIPART_FORM_DATA_TYPE));
    }
    return multipart;
  }
  
  private void showResultMessage(Response response)
  {
    if (response.getStatus() == 200)
    {
      addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your request has been sent successsfully");
    }
    else if (response.getStatus() == 406) 
    {
      addMessage(FacesMessage.SEVERITY_WARN, "Error", "Your request already exists");
    }
    else if (response.getStatus() == 500) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Warning", "There was some problem with the server. Please try again in a few minutes.");
    }
    else if (response.getStatus() == 400) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", response.getEntity().toString());
    }
    else if (response.getStatus() == 301) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", "You shouldn't see this");
    }
    else 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Warning", "Some undefined problem ocured during sending renew request: "+response.readEntity(String.class));
    }
  }
  
  private static void addMessage(Severity severity, String summary, String detail)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    context.addMessage(null, new FacesMessage(severity, summary, detail));
  }
  
  private static Client createClient()
  {
    Client httpClient = ClientBuilder.newClient();
    httpClient.register(JacksonJsonProvider.class);
    httpClient.register(MultiPartFeature.class);
    return httpClient;
  }
  
  private static String getUri(String servletContext)
  {
    String base = "http://license-order.axonivy.io/ivy/";
    String application = System.getProperty("test.engine.app", IApplication.DESIGNER_APPLICATION_NAME);
    return base+servletContext+"/"+application+"/renewLicense";
  }
}
