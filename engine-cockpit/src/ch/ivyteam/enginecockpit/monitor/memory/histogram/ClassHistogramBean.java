package ch.ivyteam.enginecockpit.monitor.memory.histogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.log.Logger;

@ManagedBean
@SessionScoped
public class ClassHistogramBean {

  static final ObjectName DIAGNOSTIC_COMMAND = createDiagnosticCommand();
  static final ObjectName HOT_SPOT_DIAGNOSTIC = createHotSpotDiagnostic();
  private static final Logger LOGGER = Logger.getPackageLogger(ClassHistogramBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);
  private Map<String, ClassHisto> history = new HashMap<>();
  private List<ClassHisto> classes = new ArrayList<>();
  private List<ClassHisto> filteredClasses;
  private String filter;
  private long maxDeltaToMinInstances;
  private long maxInstances;
  private long maxBytes;

  public ClassHistogramBean() {}

  public void refresh() {
    try {
      var dump = (String) ManagementFactory.getPlatformMBeanServer().invoke(DIAGNOSTIC_COMMAND,
          "gcClassHistogram", new Object[] {new String[] {}}, new String[] {"[Ljava.lang.String;"});
      var newClasses = ClassHisto.parse(this, dump);
      history = merge(history, newClasses);
      classes = new ArrayList<>(history.values());
      maxDeltaToMinInstances = classes.stream().mapToLong(ClassHisto::getDeltaToMinInstances).max().orElse(0);
      maxInstances = classes.stream().mapToLong(ClassHisto::getInstances).max().orElse(0);
      maxBytes = classes.stream().mapToLong(ClassHisto::getBytes).max().orElse(0);
    } catch (Exception ex) {
      HANDLER.showError("Could not get class histogram", ex);
    }
  }

  public void clear() {
    history.clear();
    classes.clear();
    if (filteredClasses != null) {
      filteredClasses = null;
    }
    filter = null;
  }

  public StreamedContent dumpMemory() throws InstanceNotFoundException, ReflectionException, MBeanException, IOException {
    var dumpDir = Files.createTempDirectory("memoryDump");
    var dumpName = Advisor.getAdvisor().getApplicationName() + " Memory Dump";
    var dumpFile = dumpDir.resolve(dumpName + ".hprof");
    ManagementFactory.getPlatformMBeanServer().invoke(HOT_SPOT_DIAGNOSTIC,
        "dumpHeap", new Object[] {dumpFile.toAbsolutePath().toString(), true}, new String[] {String.class.getName(), boolean.class.getName()});
    var zipFile = dumpDir.resolve(dumpName + ".zip");
    zip(dumpFile, zipFile);
    Files.delete(dumpFile);

    return DefaultStreamedContent.builder().name(zipFile.getFileName().toString())
        .stream(() -> openTempFileInputStream(zipFile))
        .contentLength(Files.size(zipFile))
        .build();
  }

  private void zip(Path dumpFile, Path zipFile) throws IOException {
    try (var out = Files.newOutputStream(zipFile); var zipOut = new ZipOutputStream(out)) {
      try (var in = Files.newInputStream(dumpFile)) {
        var entry = new ZipEntry(dumpFile.getFileName().toString());
        entry.setTime(Files.getLastModifiedTime(dumpFile).toMillis());
        zipOut.putNextEntry(entry);
        in.transferTo(zipOut);
      }
    }
  }

  private InputStream openTempFileInputStream(Path dumpFile) {
    try {
      return new TempFileInputStream(dumpFile);
    } catch (IOException ex) {
      throw new UncheckedIOException("Cannot open memory dump file", ex);
    }
  }

  public boolean isNotClearable() {
    return history.isEmpty();
  }

  private static Map<String, ClassHisto> merge(Map<String, ClassHisto> history, Map<String, ClassHisto> newClasses) {
    if (history == null) {
      return newClasses;
    }
    for (var newClass : newClasses.values()) {
      var oldClass = history.get(newClass.getId());
      if (oldClass == null) {
        history.put(newClass.getId(), newClass);
      } else {
        oldClass.update(newClass);
      }
    }
    return history;
  }

  public List<ClassHisto> getClasses() {
    return classes;
  }

  public void setClasses(List<ClassHisto> classes) {
    this.classes = classes;
  }

  public List<ClassHisto> getFilteredClasses() {
    return filteredClasses;
  }

  public void setFilteredClasses(List<ClassHisto> filteredClasses) {
    this.filteredClasses = filteredClasses;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  long getMaxDeltaToMinInstances() {
    return maxDeltaToMinInstances;
  }

  long getMaxInstances() {
    return maxInstances;
  }

  long getMaxBytes() {
    return maxBytes;
  }

  private static ObjectName createDiagnosticCommand() {
    try {
      return new ObjectName("com.sun.management", "type", "DiagnosticCommand");
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create DiagnosticCommand name", ex);
      return null;
    }
  }

  private static ObjectName createHotSpotDiagnostic() {
    try {
      return new ObjectName("com.sun.management", "type", "HotSpotDiagnostic");
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create HotSpotDiagnostic name", ex);
      return null;
    }
  }

  private static class TempFileInputStream extends InputStream {

    private final InputStream is;
    private final Path dumpFile;
    public TempFileInputStream(Path dumpFile) throws IOException {
      this.dumpFile = dumpFile;
      is = Files.newInputStream(dumpFile);
    }

    @Override
    public int read() throws IOException {
      return is.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
      return is.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      return is.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
      is.close();
      Files.delete(dumpFile);
      Files.delete(dumpFile.getParent());
    }
  }
}
