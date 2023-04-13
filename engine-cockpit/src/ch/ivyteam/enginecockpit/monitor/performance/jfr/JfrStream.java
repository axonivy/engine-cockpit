package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;
import javax.management.openmbean.TabularData;

class JfrStream extends InputStream {
  private final long streamId;
  private byte[] buffer;
  private int nextIndexToRead = 0;
  private IOException error;

  JfrStream(Recording recording) {
    this.streamId = open(recording);
  }

  private long open(Recording recording) {
    try {
      return (long)ManagementFactory.getPlatformMBeanServer().invoke(JfrBean.FLIGHT_RECORDER, "openStream", new Object[]{recording.id(), null}, new String[]{long.class.getName(), TabularData.class.getName()});
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      this.error = new IOException("Cannot open JFR stream for recording '" + recording.id() + "'", ex);
      return -1;
    }
  }

  @Override
  public int read() throws IOException {
    if (error != null) {
      throw error;
    }
    if (nextIndexToRead == -1) {
      return nextIndexToRead;
    }
    if (buffer == null || nextIndexToRead >= buffer.length) {
      try {
        buffer = (byte[])ManagementFactory.getPlatformMBeanServer().invoke(JfrBean.FLIGHT_RECORDER, "readStream", new Object[]{streamId}, new String[]{long.class.getName()});
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        throw new IOException("Cannot read JFR stream", ex);
      }
      if (buffer == null) {
        nextIndexToRead = -1;
        return nextIndexToRead;
      }
      nextIndexToRead = 0;
    }
    return Byte.toUnsignedInt(buffer[nextIndexToRead++]);
  }
  
  @Override
  public void close() throws IOException {
    if (error != null) {
      throw error;
    }
    try {
      ManagementFactory.getPlatformMBeanServer().invoke(JfrBean.FLIGHT_RECORDER, "closeStream", new Object[]{streamId}, new String[]{long.class.getName()});
    } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
      throw new IOException("Cannot close JFR stream", ex); 
    }
  }
}
