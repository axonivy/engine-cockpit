package ch.ivyteam.enginecockpit.fileupload;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.enginecockpit.util.LicenceUtil;

@Path("licence")
public class FileUploadService
{
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadLicence(@FormDataParam("licence") InputStream stream,
          @FormDataParam("licence") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(),
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    try
    {
      LicenceUtil.verifyAndInstall(httpFile);
    }
    catch (Exception ex)
    {
      return Response.status(500).entity(ex.getMessage()).build();
    }
    return Response.status(200).entity("Successfully uploaded licence").build();
  }
}
