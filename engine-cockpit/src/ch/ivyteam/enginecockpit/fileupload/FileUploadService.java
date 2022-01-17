package ch.ivyteam.enginecockpit.fileupload;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.application.branding.BrandingIO;
import ch.ivyteam.ivy.config.NewLicenceFileInstaller;
import io.swagger.v3.oas.annotations.Hidden;

@SuppressWarnings("restriction")
@Path("upload")
@Hidden
public class FileUploadService {
  @POST
  @Path("licence")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadLicence(@FormDataParam("licence") InputStream stream,
          @FormDataParam("licence") FormDataContentDisposition fileDetail) {
    try {
      NewLicenceFileInstaller.install(fileDetail.getFileName(), stream);
    } catch (Exception ex) {
      return Response.status(500).entity(ex.getMessage()).build();
    }
    return Response.status(200).entity("Successfully uploaded licence").build();
  }

  @POST
  @Path("branding")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadBrandingRes(@FormDataParam("resource") InputStream stream,
          @FormDataParam("resource") FormDataContentDisposition fileDetail,
          @FormDataParam("oldResourceName") String oldResourceName,
          @FormDataParam("appName") String appName) {
    var newResourceName = fileDetail.getFileName();
    try {
      var app = IApplicationConfigurationManager.instance().findApplication(appName);
      newResourceName = new BrandingIO(app).setImage(oldResourceName,
              FilenameUtils.getExtension(fileDetail.getFileName()), stream);
    } catch (Exception ex) {
      return Response.status(500).entity(ex.getMessage()).build();
    }
    return Response.status(200).entity("Successfully uploaded " + newResourceName).build();
  }

}
