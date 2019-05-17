package ch.ivyteam.enginecockpit.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ch.ivyteam.enginecockpit.util.LicenceUtil;
import ch.ivyteam.ivy.environment.Ivy;

@Path("upload")
public class FileUploadService
{

  @POST
  @Path("/file")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response post(@FormDataParam("deployoptions") String deployoptions,
          @FormDataParam("file") InputStream stream,
          @FormDataParam("file") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(),
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    //TODO: send deployoptions
    Ivy.log().info(deployoptions);
    //FileUploadRequest fileUploadRequest = new FileUploadRequest(title, description, httpFile);
    try
    {
      File file = new File(Files.createTempDirectory("temp").toFile(), httpFile.getSubmittedFileName());
      Ivy.log().info(file);
      FileUtils.copyInputStreamToFile(httpFile.getStream(), file);
    }
    catch (IOException ex)
    {
      return Response.status(200).entity(new FileUploadResponse("error", ex.getMessage())).build();
    }
    return Response.status(200).entity(new FileUploadResponse("ok", "Successfully deployed " + httpFile.getSubmittedFileName())).build();
  }

  @POST
  @Path("/licence")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadLicence(@FormDataParam("file") InputStream stream,
          @FormDataParam("file") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(),
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    try
    {
      LicenceUtil.verifyAndInstall(httpFile);
    }
    catch (Exception ex)
    {
      return Response.status(200).entity(new FileUploadResponse("error", ex.getMessage())).build();
    }
    return Response.status(200).entity(new FileUploadResponse("ok", "Successfully uploaded licence")).build();
  }
}
