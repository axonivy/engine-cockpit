package ch.ivyteam.enginecockpit.renewlicence;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.licence.SignedLicence;

@ManagedBean
@RequestScoped
public class RenewLicence
{
  
  private String mailAddress;
  
  public String getMailAddress()
  {
    return mailAddress;
  }
  
  public void setMailAddress(String mailAddress)
  {
    this.mailAddress = mailAddress;
  }
  
  public void send()
  {
    if (!EmailUtil.validateEmailAddress(mailAddress)) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Your email address is not valid");
      return;
    }
    showResultMessage(executeCall());
  }

  private Response executeCall()
  {
    try
    {
      FormDataMultiPart multipart = createMultipart(SignedLicence.getLicenceContent());
      return createClient().target(getUri()).request()
              .header("X-Requested-By", "ivy")
              .header("MIME-Version", "1.0")
              .header("mailTo", mailAddress)
              .put(Entity.entity(multipart, multipart.getMediaType()));
    }
    catch (Exception ex)
    {
      return Response.status(400).entity("There was problem with requesting response ").build();
    }
  }

  private FormDataMultiPart createMultipart(String licContent) throws IOException
  {
    try (FormDataMultiPart formDataMultiPart = new FormDataMultiPart())
    {
      FormDataBodyPart filePart = new FormDataBodyPart("oldLicense", licContent);
      FormDataMultiPart multipart = (FormDataMultiPart) formDataMultiPart.field("oldLicense", licContent,
              MediaType.MULTIPART_FORM_DATA_TYPE).bodyPart(filePart);
      multipart.setMediaType(Boundary.addBoundary(MediaType.MULTIPART_FORM_DATA_TYPE));
      return multipart;
    }
  }
  
  private void showResultMessage(Response response)
  {
    if (response.getStatus() == 200)
    {
      addMessage(FacesMessage.SEVERITY_INFO, "Success", "Your request has been sent successsfully");
    }
    else if (response.getStatus() == 406) 
    {
      addMessage(FacesMessage.SEVERITY_WARN, "Warning", "Your request already exists");
    }
    else if (response.getStatus() == 500) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", "There was some problem with the server. Please try again in a few minutes.");
    }
    else if (response.getStatus() == 400) 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Response: "+response.readEntity(String.class));
    }
    else 
    {
      addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Some undefined problem ocured during sending renew request: "+response.readEntity(String.class));
    }
  }
  
  private static void addMessage(Severity severity, String summary, String detail)
  {
    FacesContext context = FacesContext.getCurrentInstance();
    context.addMessage("msgs", new FacesMessage(severity, summary, detail));
  }
  
  private static Client createClient()
  {
    Client httpClient = ClientBuilder.newClient();
    httpClient.register(JacksonJsonProvider.class);
    httpClient.register(MultiPartFeature.class);
    return httpClient;
  }
  
  private static String getUri()
  {
    return System.getProperty("licence.base.uri", "https://license-order.axonivy.io/ivy/api/LicenseOrder")+"/renewLicense";
  }
}
