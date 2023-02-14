package ch.ivyteam.enginecockpit.monitor;

import java.io.ByteArrayInputStream;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.ObjectName;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.monitor.trace.BackgroundMeterUtil;

@ManagedBean
@SessionScoped
public class ThreadBean {

  private List<Info> threads;
  private String filter = "";
  private long[] deadLocked;
  private boolean isCpuEnabled;
  private long maxCpuTime;
  private long maxUserTime;

  public ThreadBean() {
    refresh();
  }

  public void refresh() {
    isCpuEnabled = threadMxBean().isThreadCpuTimeSupported() && threadMxBean().isThreadCpuTimeEnabled();
    deadLocked = threadMxBean().findDeadlockedThreads();
    ThreadInfo[] infos = threadMxBean().dumpAllThreads(false, false);
    maxCpuTime = Stream.of(infos).map(this::toCpuTime).max(Long::compareTo).orElse(-1L);
    maxUserTime = Stream.of(infos).map(this::toUserTime).max(Long::compareTo).orElse(-1L);
    threads = Stream.of(infos).map(info -> infoFor(info)).collect(Collectors.toList());
  }

  public StreamedContent dump() {
    String dump = dumpToString();
    return DefaultStreamedContent.builder().name("ThreadDump.txt").contentType("text/text")
            .stream(() -> new ByteArrayInputStream(dump.getBytes(StandardCharsets.UTF_8))).build();
  }

  private String dumpToString() {
    String dump;
    try {
      dump = (String) ManagementFactory.getPlatformMBeanServer().invoke(
              new ObjectName("com.sun.management", "type", "DiagnosticCommand"), "threadPrint",
              new Object[] {new String[0]}, new String[] {String[].class.getName()});
    } catch (Exception ex) {
      dump = "Cannot create a thread dump because of:\n" + ExceptionUtils.getFullStackTrace(ex);
    }
    return dump;
  }

  public List<Info> getThreads() {
    return threads;
  }

  public List<Info> getFilteredThreads() {
    return getThreads().stream().filter(this::filter).collect(Collectors.toList());
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  private boolean filter(Info threadInfo) {
    String name = threadInfo.getName();
    return name != null && StringUtils.containsIgnoreCase(name, filter);
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
    return new Info(info.getThreadId(), info.getThreadName(), info.getThreadState(), info.getPriority(), cpuTime, userTime);
  }

  public final class Info {
    private final long id;
    private final String name;
    private final State state;
    private final int priority;
    private final long cpuTime;
    private final long userTime;

    private Info(long id, String name, State state, int priority, long cpuTime, long userTime) {
      super();
      this.id = id;
      this.name = name;
      this.state = state;
      this.priority = priority;
      this.cpuTime = cpuTime;
      this.userTime = userTime;
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

    public String getStateColor() {
      return switch(state) {
        case RUNNABLE -> "green";
        case BLOCKED -> isDeadLocked() ? "red" : "orange";
        case WAITING -> "blue";
        case TIMED_WAITING -> "blue";
        case NEW -> "black";
        case TERMINATED -> "black";
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
      return deadLocked != null && Arrays.stream(deadLocked).anyMatch(threadId -> threadId == id);
    }
  }
}
