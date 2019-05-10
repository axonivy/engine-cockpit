package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.UrlUtil;

public class LogView
{
  private File file;
  private String fileName;
  private String content;
  private boolean downloadEnabled = true;
  
  public LogView(String logName)
  {
    fileName = logName;
    file = UrlUtil.getLogFile(logName);
    content = readContent();
  }
  
  public String getFileName()
  {
    return fileName;
  }

  private String readContent()
  {
    List<String> lines = new ArrayList<>();
    try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8))
    {
      String line = reader.readLine();
      int count = 0;
      while(line != null && count < 1000)
      {
        lines.add(0, line);
        count ++;
        line = reader.readLine();
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }
    if (lines.isEmpty())
    {
      downloadEnabled = false;
      return "Logfile '" + fileName + "' don't exists or is empty.";
    }
    return lines.stream().collect(Collectors.joining("\n"));
  }
  
  public String getContent()
  {
    return content;
  }
  
  public boolean isDownloadEnabled()
  {
    return downloadEnabled;
  }
  
  public StreamedContent getFile() throws IOException 
  {
    InputStream newInputStream = Files.newInputStream(file.toPath());
    return new DefaultStreamedContent(newInputStream, "text/plain", fileName);
  }
  
}
