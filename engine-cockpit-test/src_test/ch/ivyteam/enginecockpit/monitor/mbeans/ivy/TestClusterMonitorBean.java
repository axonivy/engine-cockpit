package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestClusterMonitorBean
{ 
  @AfterEach
  public void afterEach()
  {
    MBeans.unregisterAllMBeans();
  }
    
  @Test
  public void sendMessages()
  {
    MBeans.registerMBeanFor(new Cluster());
    var testee = new ClusterMonitorBean();
    
    var series = testee.getSendMessagesMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var mails = series.get(0);
    assertThat(mails.getLabel()).isEqualTo("Sent");
    assertThat(mails.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    assertThat(testee.getSendMessagesMonitor().getInfo()).isEqualTo("Sent Messages: -, Total 3, Errors -, Errors Total 4");
  }

  @Test
  public void sendProcessingTimeMonitor()
  {
    MBeans.registerMBeanFor(new Cluster());
    var testee = new ClusterMonitorBean();
    
    var series = testee.getSendProcessingTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(3);
    
    var min = series.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(5.0D)); 
    
    var avg = series.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var max = series.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(7.0D)); 
    
    assertThat(testee.getSendProcessingTimeMonitor().getInfo()).isEqualTo("Send Processing Time: Min 5 us, Avg -, Max 7 us, Total 6 us");
  }
  
  @Test
  public void receiveMessages()
  {
    MBeans.registerMBeanFor(new Cluster());
    var testee = new ClusterMonitorBean();
    
    var series = testee.getReceiveMessagesMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var mails = series.get(0);
    assertThat(mails.getLabel()).isEqualTo("Received");
    assertThat(mails.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    assertThat(testee.getReceiveMessagesMonitor().getInfo()).isEqualTo("Received Messages: -, Total 10, Errors -, Errors Total 11");
  }

  @Test
  public void receiveProcessingTimeMonitor()
  {
    MBeans.registerMBeanFor(new Cluster());
    var testee = new ClusterMonitorBean();
    
    var series = testee.getReceiveProcessingTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(3);
    
    var min = series.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(12.0D)); 
    
    var avg = series.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var max = series.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(14.0D)); 
    
    assertThat(testee.getReceiveProcessingTimeMonitor().getInfo()).isEqualTo("Receive Processing Time: Min 12 us, Avg -, Max 14 us, Total 13 us");
  }

  @MBean("ivy Engine:type=Cluster Channel")
  private static final class Cluster
  {    
    @MAttribute
    public long getSendMessages()
    {
      return 3;
    }
    
    @MAttribute
    public long getSendErrors()
    {
      return 4;
    }
    
    @MAttribute
    public long getSendMessagesMinExecutionTimeDeltaInMicroSeconds()
    {
      return 5;
    }

    @MAttribute
    public long getSendMessagesTotalExecutionTimeInMicroSeconds()
    {
      return 6;
    }

    @MAttribute
    public long getSendMessagesMaxExecutionTimeDeltaInMicroSeconds()
    {
      return 7;
    }

    @MAttribute
    public long getReceiveMessages()
    {
      return 10;
    }
    
    @MAttribute
    public long getReceiveErrors()
    {
      return 11;
    }
    
    @MAttribute
    public long getReceiveMessagesMinExecutionTimeDeltaInMicroSeconds()
    {
      return 12;
    }

    @MAttribute
    public long getReceiveMessagesTotalExecutionTimeInMicroSeconds()
    {
      return 13;
    }

    @MAttribute
    public long getReceiveMessagesMaxExecutionTimeDeltaInMicroSeconds()
    {
      return 14;
    }
  }
}
