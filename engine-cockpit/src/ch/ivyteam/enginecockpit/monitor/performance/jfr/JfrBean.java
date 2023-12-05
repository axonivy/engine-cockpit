package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class JfrBean {

  static final ObjectName FLIGHT_RECORDER = createFlightRecorderName();

  private static final Logger LOGGER = Logger.getPackageLogger(JfrBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);


  private List<Recording> recordings;
  private List<Configuration> configurations;

  private String configuration;

  private String name = Advisor.instance().getApplicationName();
  private List<Option> options = Option.createAll();

  public JfrBean() {
    refresh();
    configurations = queryConfigurations();
    if (!configurations.isEmpty()) {
      configuration = configurations.get(0).name();
    }
  }

  public List<Configuration> getConfigurations() {
    return configurations;
  }

  public String getConfiguration() {
    return configuration;
  }

  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  private Configuration configuration() {
    return configurations
        .stream()
        .filter(config -> Objects.equals(config.name(), configuration))
        .findAny()
        .orElseThrow();
  }

  public void addConfiguration(FileUploadEvent event) {
    try {
      var uploadedConfiguration = Configuration.from(event.getFile());
      if (uploadedConfiguration != null) {
        configurations.add(uploadedConfiguration);
        setConfiguration(uploadedConfiguration.getName());
      }
    } catch (Exception ex) {
      HANDLER.showError("Cannot upload configuration file", ex);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Recording> getRecordings() {
    return recordings;
  }

  public boolean isHasRunningRecordings() {
    return recordings.stream().anyMatch(Recording::isRunning);
  }

  public boolean isHasNotRunningRecordings() {
    return recordings.stream().anyMatch(Recording::isNotRunning);
  }

  public void refresh() {
    recordings = queryRecordings();
  }

  public void startRecording() {
    try {
      var id = ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "newRecording", null, null);
      configureRecording(id);
      configureOptions(id);
      ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "startRecording", new Object[]{id}, new String[]{long.class.getName()});
      refresh();
    } catch (InstanceNotFoundException | ReflectionException | MBeanException | OpenDataException ex) {
      HANDLER.showError("Cannot start recording '" + name + "'", ex);
    }
  }

  private void configureRecording(Object id)
          throws ReflectionException, MBeanException, InstanceNotFoundException
  {
    var config = configuration();
    if (config.contents() != null) {
      ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "setConfiguration", new Object[] {id, config.contents()}, new String[] {long.class.getName(), String.class.getName()});
    } else {
      ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "setPredefinedConfiguration", new Object[] {id, config.name()}, new String[] {long.class.getName(), String.class.getName()});
    }
  }

  private void configureOptions(Object id)
          throws ReflectionException, MBeanException, InstanceNotFoundException, OpenDataException {
    var originalOptions = (TabularData)ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "getRecordingOptions", new Object[] {id}, new String[] {long.class.getName()});
    var opts = new TabularDataSupport(originalOptions.getTabularType());
    var originalNameOption = originalOptions.get(new Object[] {"name"});
    var nameOption = new CompositeDataSupport(originalNameOption.getCompositeType(), Map.of("key", "name", "value", name));
    opts.put(nameOption);
    for (var opt : options) {
      if (!opt.isDefaultValue()) {
        var o = new CompositeDataSupport(originalNameOption.getCompositeType(), Map.of("key", opt.getName(), "value", opt.getValue()));
        opts.put(o);
      }
    }
    ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "setRecordingOptions", new Object[] {id, opts}, new String[] {long.class.getName(), TabularData.class.getName()});
  }

  public List<Option> getOptions() {
    return options;
  }

  public void stopRecording(Recording recording) {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "stopRecording", new Object[]{recording.id()}, new String[]{long.class.getName()});
      refresh();
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot stop recording '"+recording.name()+"'", ex);
    }
  }

  public void closeRecording(Recording recording) {
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(FLIGHT_RECORDER, "closeRecording", new Object[]{recording.id()}, new String[]{long.class.getName()});
      refresh();
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot close recording '"+recording.name()+"'", ex);
    }
  }

  public StreamedContent downloadRecording(Recording recording) {
    return DefaultStreamedContent.builder()
        .name(recording.name()+".jfr")
        .stream(() -> new JfrStream(recording))
        .build();
  }

  public static List<Recording> queryRecordings() {
    try {
      var recordings = (CompositeData[])ManagementFactory.getPlatformMBeanServer().getAttribute(FLIGHT_RECORDER, "Recordings");
      return Stream.of(recordings)
          .map(Recording::from)
          .collect(Collectors.toList());
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot read attribute 'Recordings' from flight recorder", ex);
      return List.of();
    }
  }

  private static List<Configuration> queryConfigurations() {
    try {
      var configurations = (CompositeData[])ManagementFactory.getPlatformMBeanServer().getAttribute(FLIGHT_RECORDER, "Configurations");
      return new ArrayList<>(Stream.of(configurations)
          .map(Configuration::from)
          .toList());
    } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
      HANDLER.showError("Cannot read attribute 'Configurations' from flight recorder", ex);
      return List.of();
    }
  }

  private static ObjectName createFlightRecorderName() {
    try {
      return new ObjectName("jdk.management.jfr", "type", "FlightRecorder");
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create FlightRecorder name", ex);
      return null;
    }
  }
}
