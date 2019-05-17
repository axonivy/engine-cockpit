package ch.ivyteam.enginecockpit.fileupload;

public class FileUploadRequest
{
  private final String title;
  private final String description;
  private final HttpFile httpFile;

  public FileUploadRequest(String title, String description, HttpFile httpFile) {
      this.title = title;
      this.description = description;
      this.httpFile = httpFile;
  }

  public String getTitle() {
      return title;
  }

  public String getDescription() {
      return description;
  }

  public HttpFile getHttpFile() {
      return httpFile;
  }
}
