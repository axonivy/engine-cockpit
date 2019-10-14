package renewLicenceService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.security.PermitAll;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.api.API;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.licence.Licence;

@Path("test/renewLicense")
public class TestRenewLicenceService
{
  @PermitAll
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response create(@FormDataParam("oldLicense") InputStream oldLicenseStream,
          @FormDataParam("oldLicense") FormDataContentDisposition oldLicenseDetail)
  {
    try
    {
      API.checkNotNull(oldLicenseDetail, "oldLicenseDetail");
      Licence oldLicence = new Licence("", "");
      oldLicence.readFromAscii(IOUtils.toString(oldLicenseStream, StandardCharsets.US_ASCII));
      checkIfEmpty(oldLicence);
    }
    catch (Exception ex)
    {
      Ivy.log().info("Exception in renew licence service: "+ex);
    }
    return Response.status(301).entity("This is for testing").build();
  }

  private void checkIfEmpty(Licence oldLicence)
  {
    if (new Licence("", "").equals(oldLicence))
    {
      throw new BadRequestException("This is an empty license");
    }
  }
}