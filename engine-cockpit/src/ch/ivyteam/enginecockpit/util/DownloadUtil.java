package ch.ivyteam.enginecockpit.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadUtil
{
  
  public static void zipDir(Path source, OutputStream out) throws IOException
  {
    try (var zs = new ZipOutputStream(out))
    {
      Files.walk(source)
              .filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                var zipEntry = new ZipEntry(source.relativize(path).toString());
                try
                {
                  zs.putNextEntry(zipEntry);
                  Files.copy(path, zs);
                  zs.closeEntry();
                }
                catch (IOException e)
                {
                  System.err.println(e);
                }
              });
    }
  }

}
