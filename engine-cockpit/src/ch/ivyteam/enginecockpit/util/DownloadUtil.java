package ch.ivyteam.enginecockpit.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.ivyteam.log.Logger;

public class DownloadUtil {

  private static final Logger LOGGER = Logger.getLogger(DownloadUtil.class);

  public static void zipDir(OutputStream out, List<Path> sources) throws IOException {
    try (var zs = new ZipOutputStream(out)) {
      for (var source : sources) {
        try (var walker = Files.walk(source)) {
          walker.filter(path -> !Files.isDirectory(path))
              .forEach(path -> {
                var zipEntry = new ZipEntry(source.relativize(path).toString());
                try {
                  zs.putNextEntry(zipEntry);
                  Files.copy(path, zs);
                  zs.closeEntry();
                } catch (IOException ex) {
                  LOGGER.info(ex);
                }
              });
        }
      }
    }
  }

  public static InputStream getFileStream(Path file) {
    try {
      return new FileInputStream(file.toFile()){
        @Override
        public void close() throws IOException {
          super.close();
          try {
            Files.delete(file);
          } catch (Exception ex) {
            LOGGER.info("Could not delete file '" + file.getFileName() + "' after closing stream : ", ex);
          }
        }
      };
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }
}
