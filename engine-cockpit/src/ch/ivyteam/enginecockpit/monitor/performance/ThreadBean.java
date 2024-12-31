package ch.ivyteam.enginecockpit.monitor.performance;

import java.io.ByteArrayInputStream;
import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.monitor.trace.BackgroundMeterUtil;

@ManagedBean
@SessionScoped
public class ThreadBean {

  private List<Info> threads;
  private List<Info> filteredThreads;
  private long[] deadLocked;
  private boolean isCpuEnabled;
  private long maxCpuTime;
  private long maxUserTime;
  private Info selected;
  private Long threadId;
  private String filter;

  public ThreadBean() {
    refresh();
  }

  public void setThreadId(Long threadId) {
    this.threadId = threadId;
    refresh();
    this.filter = Long.toString(threadId);
  }

  public Long getThreadId() {
    return threadId;
  }

  public String getFilter() {
    var f = filter;
    filter = null;
    return f;
  }

  public void refresh() {
    isCpuEnabled = threadMxBean().isThreadCpuTimeSupported() && threadMxBean().isThreadCpuTimeEnabled();
    deadLocked = threadMxBean().findDeadlockedThreads();
    ThreadInfo[] infos = threadMxBean().dumpAllThreads(true, true);
    maxCpuTime = Stream.of(infos).map(this::toCpuTime).max(Long::compareTo).orElse(-1L);
    maxUserTime = Stream.of(infos).map(this::toUserTime).max(Long::compareTo).orElse(-1L);
    threads = Stream.of(infos).map(this::infoFor).collect(Collectors.toList());
    filteredThreads = threads;
  }

  public StreamedContent dump() {
    String dump = dumpToString();
    if (dump == null) {
      return null;
    }
    return DefaultStreamedContent.builder().name("ThreadDump.txt").contentType("text/text")
        .stream(() -> new ByteArrayInputStream(dump.getBytes(StandardCharsets.UTF_8))).build();
  }

  private String dumpToString() {
    try {
      return (String) ManagementFactory.getPlatformMBeanServer().invoke(
          new ObjectName("com.sun.management", "type", "DiagnosticCommand"), "threadPrint",
          new Object[] {new String[0]}, new String[] {String[].class.getName()});
    } catch (Exception ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
      return null;
    }
  }

  public List<Info> getThreads() {
    return threads;
  }

  public List<Info> getFilteredThreads() {
    return filteredThreads;
  }

  public void setFilteredThreads(List<Info> filteredThreads) {
    this.filteredThreads = filteredThreads;
  }

  @SuppressWarnings("hiding")
  public boolean filter(Object value, Object filter, @SuppressWarnings("unused") Locale locale) {
    if (value instanceof Info info) {
      try {
        var id = Long.valueOf(filter.toString());
        return info.getId() == id;
      } catch (NumberFormatException ex) {}
      String name = info.getName();
      if (name != null && StringUtils.containsIgnoreCase(name, filter.toString())) {
        return true;
      }
    }
    return false;
  }

  public Info getSelected() {
    return this.selected;
  }

  public void setSelected(Info thread) {
    this.selected = thread;
  }

  private static ThreadMXBean threadMxBean() {
    return ManagementFactory.getThreadMXBean();
  }

  private long toCpuTime(ThreadInfo info) {
    return isCpuEnabled ? threadMxBean().getThreadCpuTime(info.getThreadId()) : -1;
  }

  private long toUserTime(ThreadInfo info) {
    return isCpuEnabled ? threadMxBean().getThreadUserTime(info.getThreadId()) : -1;
  }

  private Info infoFor(ThreadInfo info) {
    var cpuTime = toCpuTime(info);
    var userTime = toUserTime(info);
    return new Info(info, cpuTime, userTime);
  }

  public final class Info {

    private final long id;
    private final String name;
    private final State state;
    private final int priority;
    private final long cpuTime;
    private final long userTime;
    private final ThreadInfo info;

    private Info(ThreadInfo info, long cpuTime, long userTime) {
      this.id = info.getThreadId();
      this.name = info.getThreadName();
      this.state = info.getThreadState();
      this.priority = info.getPriority();
      this.cpuTime = cpuTime;
      this.userTime = userTime;
      this.info = info;
    }

    public long getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public State getState() {
      return state;
    }

    public String getStateColorClass() {
      return switch (state) {
        case RUNNABLE -> "thread-runnable";
        case BLOCKED -> isDeadLocked() ? "thread-deadlocked" : "thread-waiting";
        case WAITING -> isDeadLocked() ? "thread-deadlocked" : "thread-waiting";
        case TIMED_WAITING -> "thread-waiting";
        case NEW -> "";
        case TERMINATED -> "";
      };
    }

    public String getStateTitle() {
      if (isDeadLocked()) {
        return "This thread is deadlocked! It is waiting to lock " + getLockName()
            + " which is owned by thread " + getLockOwner() + ".";
      }
      return switch (state) {
        case RUNNABLE -> "Thread is runnable.";
        case BLOCKED -> "Thread is blocked. It waits to lock " + getLockName() + " which is owned by thread "
            + getLockOwner() + ".";
        case WAITING -> "Thread is waiting on lock " + getLockName() + ".";
        case TIMED_WAITING -> "Thread is waiting with a timeout on lock " + getLockName() + ".";
        case NEW -> "Thread is new and not yet started";
        case TERMINATED -> "Thread has terminated";
      };
    }

    public int getPriority() {
      return priority;
    }

    public long getCpuTime() {
      return cpuTime;
    }

    public String getCpuTimeBackground() {
      return BackgroundMeterUtil.background(cpuTime, maxCpuTime);
    }

    public long getUserTime() {
      return userTime;
    }

    public String getUserTimeBackground() {
      return BackgroundMeterUtil.background(userTime, maxUserTime);
    }

    public boolean isDeadLocked() {
      return deadLocked != null && Arrays.stream(deadLocked).anyMatch(tId -> tId == id);
    }

    public ThreadInfo getInfo() {
      return info;
    }

    public String getLockedSynchronizers() {
      var lockedSynchronizers = info.getLockedSynchronizers();
      if (lockedSynchronizers == null || lockedSynchronizers.length == 0) {
        return "None";
      }
      return Stream.of(lockedSynchronizers).map(LockInfo::toString).collect(Collectors.joining(",\n"));
    }

    public String getLockedMonitors() {
      var lockedMonitors = info.getLockedMonitors();
      if (lockedMonitors == null || lockedMonitors.length == 0) {
        return "None";
      }
      return Stream.of(lockedMonitors).map(this::toMonitorString).collect(Collectors.joining(",\n"));
    }

    private String toMonitorString(MonitorInfo monitor) {
      var frame = monitor.getLockedStackFrame();
      return monitor.toString() + " - " + frame.getClassName() + "." + frame.getMethodName() + "("
          + frame.getFileName() + ":" + frame.getLineNumber() + ")";
    }

    public String getLockName() {
      var lockName = info.getLockName();
      if (StringUtils.isBlank(lockName)) {
        return "None";
      }
      return lockName;
    }

    public String getLockOwner() {
      var lockOwnerName = info.getLockOwnerName();
      if (StringUtils.isBlank(lockOwnerName)) {
        return "None";
      }
      return info.getLockOwnerId() + " - " + lockOwnerName;
    }

    public String getStackTrace() {
      var lockedMonitors = info.getStackTrace();
      if (lockedMonitors == null || lockedMonitors.length == 0) {
        return "None";
      }
      return Stream.of(lockedMonitors).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }
  }
}
