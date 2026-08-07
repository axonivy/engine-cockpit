package ch.ivyteam.enginecockpit.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.error.report.ErrorReport;
import ch.ivyteam.ivy.log.provider.LogFile;
import ch.ivyteam.ivy.log.provider.LogFileRepository;
import ch.ivyteam.log.Logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class SupportBean {

  private final static Logger LOGGER = Logger.getLogger(SupportBean.class);

  public StreamedContent getSupportReport() throws IOException {
    var errorReport = ErrorReport.create().generate();
    var zippedFile = createZippedFile(errorReport);
    return createStreamedContent(zippedFile);
  }

  private Path createZippedFile(String errorReport) throws IOException {
    var tempSupportDir = Files.createTempDirectory("SupportReport");
    var zipFile = tempSupportDir.resolve("support-engine-report.zip");
    try (var fos = Files.newOutputStream(zipFile);
        var zos = new ZipOutputStream(fos)) {
      addEntryToZip(zos, "report.txt", errorReport.getBytes());
      LogFileRepository.instance().all().forEach(log -> addLogToZip(zos, log));
    }
    return zipFile;
  }

  private void addLogToZip(ZipOutputStream zos, LogFile log) {
    try {
      byte[] logContent = Files.readAllBytes(log.path());
      addEntryToZip(zos, log.name(), logContent);
    } catch (IOException ex) {
      LOGGER.info("Could not read log file '" + log + "': ", ex);
    }
  }

  private void addEntryToZip(ZipOutputStream zos, String entryName, byte[] content) throws IOException {
    var entry = new ZipEntry(entryName);
    zos.putNextEntry(entry);
    zos.write(content);
    zos.closeEntry();
  }

  private StreamedContent createStreamedContent(Path zipFile) {
    return DefaultStreamedContent.builder()
        .stream(() -> DownloadUtil.getFileStream(zipFile))
        .contentType("application/zip")
        .name("support-engine-report.zip")
        .build();
  }
}
