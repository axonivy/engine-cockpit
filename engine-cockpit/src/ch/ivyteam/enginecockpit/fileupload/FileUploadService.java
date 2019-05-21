package ch.ivyteam.enginecockpit.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.Gson;

import ch.ivyteam.enginecockpit.util.LicenceUtil;
import ch.ivyteam.ivy.deployment.DeploymentOptions;
import ch.ivyteam.ivy.environment.Ivy;

@Path("{applicationName}/upload")
public class FileUploadService
{

  @POST
  @Path("/deployment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response deploy(@FormDataParam("deployoptions") FormDataBodyPart options,
          @FormDataParam("file") InputStream stream,
          @FormDataParam("file") FormDataContentDisposition fileDetail)
  {
    HttpFile httpFile = new HttpFile(fileDetail.getName(), fileDetail.getFileName(),
            fileDetail.getSize(), fileDetail.getParameters(), stream);
    
    Ivy.log().info(getDeplomentOptions(options));
    
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

  private DeploymentOptions getDeplomentOptions(FormDataBodyPart options)
  {
    if (options == null)
    {
      return new DeployOptionsJson().getDeploymentOptions();
    }
    
    options.setMediaType(MediaType.APPLICATION_JSON_TYPE);
    String para = options.getEntityAs(String.class);
    Map<String, Object> json;
    try
    {
      json = new Gson().fromJson(para, Map.class);
    }
    catch (Exception e)
    {
      json = Collections.emptyMap();
    }
    return new DeployOptionsJson(json).getDeploymentOptions();
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
