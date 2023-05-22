package ch.ivyteam.enginecockpit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.ivyteam.log.Logger;

public class DownloadUtil
{

  private final static Logger LOGGER = Logger.getLogger(DownloadUtil.class);

  public static void zipDir(Path source, OutputStream out) throws IOException {
    try (ZipOutputStream zs = new ZipOutputStream(out)) {
      Files.walk(source)
              .filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                ZipEntry zipEntry = new ZipEntry(source.relativize(path).toString());
                try {
                  zs.putNextEntry(zipEntry);
                  Files.copy(path, zs);
                  zs.closeEntry();
                } catch (IOException e) {
                  System.err.println(e);
                }
              });
    }
  }


  public static InputStream getFileStream(File file) {
    try {
      return new FileInputStream(file) {
        @Override
        public void close() throws IOException {
          super.close();
          try {
            file.delete();
          } catch (Exception ex) {
            LOGGER.error("Could not delete file '" + file.getName() + "' after closing stream : ", ex);
          }
        }
      };
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

}
