package ch.ivyteam.enginecockpit.fileupload;

import java.io.InputStream;
import java.util.Map;

public class HttpFile
{
  private final String name;
  private final String submittedFileName;
  private final long size;
  private final Map<String, String> parameters;
  private final InputStream stream;

  public HttpFile(String name, String submittedFileName, long size, Map<String, String> parameters, InputStream stream) {
      this.name = name;
      this.submittedFileName = submittedFileName;
      this.size = size;
      this.parameters = parameters;
      this.stream = stream;
  }

  public String getName() {
      return name;
  }

  public String getSubmittedFileName() {
      return submittedFileName;
  }

  public long getSize() {
      return size;
  }

  public Map<String, String> getParameters() {
      return parameters;
  }

  public InputStream getStream() {
      return stream;
  }
}
