package ch.ivyteam.enginecockpit.fileupload;

import java.io.InputStream;
import java.nio.file.Files;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.enginecockpit.configuration.BrandingBean;
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
          @FormDataParam("resourceName") String resourceName,
          @FormDataParam("appName") String appName) {
    String name = FilenameUtils.getBaseName(resourceName) + "." + FilenameUtils.getExtension(fileDetail.getFileName());
    var brandingDir = BrandingBean.brandingDir(appName);
    var oldResource = brandingDir.resolve(resourceName);
    var newResource = brandingDir.resolve(name);
    try {
      Files.deleteIfExists(oldResource);
      Files.createDirectories(brandingDir);
      Files.copy(stream, newResource);
    } catch (Exception ex) {
      return Response.status(500).entity(ex.getMessage()).build();
    }
    return Response.status(200).entity("Successfully uploaded " + name).build();
  }

}
