package ch.ivyteam.enginecockpit.fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
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
  public Response post(@FormDataParam("title") String title,
          @FormDataParam("description") String description,
          @FormDataParam("file") InputStream stream,
          @FormDataParam("file") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(), 
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    FileUploadRequest fileUploadRequest = new FileUploadRequest(title, description, httpFile);
    try
    {
      Ivy.log().info(IOUtils.readLines(stream, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n")));
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    Ivy.log().info(httpFile.getSubmittedFileName());
    return Response
            .status(200)
            .entity("ok")
            .build();
  }
  
  @POST
  @Path("/licence")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response uploadLicence(@FormDataParam("title") String title,
          @FormDataParam("description") String description,
          @FormDataParam("file") InputStream stream,
          @FormDataParam("file") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(), 
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    FileUploadRequest fileUploadRequest = new FileUploadRequest(title, description, httpFile);
    
    try
    {
      LicenceUtil.verifyAndInstall(httpFile);
    }
    catch (Exception ex)
    {
      return Response.status(200).entity(ex.getMessage()).build();
    }
    
    return Response
            .status(200)
            .entity("Successfully uploaded licence")
            .build();
  }
}
