package ch.ivyteam.enginecockpit.services;

import java.io.InputStream;

import javax.annotation.security.PermitAll;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.api.API;
import ch.ivyteam.licence.SignedLicence;

@Path("test/renewLicense")
public class RenewLicenceService {
  @PermitAll
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response create(@FormDataParam("oldLicense") InputStream oldLicenseStream,
          @FormDataParam("oldLicense") FormDataContentDisposition oldLicenseDetail) {
    try {
      API.checkNotNull(oldLicenseDetail, "oldLicenseDetail");
      SignedLicence.load().fromInputStream(oldLicenseStream);
    } catch (Exception ex) {
      throw new BadRequestException("This is an empty license");
    }
    return Response.status(301).entity("This is for testing").build();
  }
}
