package ch.ivyteam.enginecockpit.fileupload;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileUploadResponse
{
  private final String status;
  private final String message;

  public FileUploadResponse(String status, String message)
  {
    this.status = status;
    this.message = message;
  }

  @JsonProperty("status")
  public String getStatus()
  {
    return status;
  }

  @JsonProperty("message")
  public String getMessage()
  {
    return message;
  }
}
