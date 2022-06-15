package ch.ivyteam.enginecockpit.monitor.system.overview;

import java.util.Objects;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.ResizeEvent;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.BezierConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.ivy.trace.SystemOverview.CommunicationChannel;
import ch.ivyteam.ivy.trace.SystemOverview.SystemLink;
import ch.ivyteam.ivy.trace.Tracer;


@ManagedBean
@ViewScoped
public class TrafficGraphBean {

  private DefaultDiagramModel model;
  private int width = 1100;
  private int height = 600;
  private static final int BORDER = 20;
  private static final int ELEMENT_HEIGHT = 68;
  private static final int ELEMENT_WIDTH = 208;

  public TrafficGraphBean() {
    refresh();
  }

  public void refresh() {
    model = new DefaultDiagramModel();
    model.setMaxConnections(-1);
    model.setContainment(false);


    var systemOverview = Tracer.instance().systemOverview();
    var inOutMax = Math.max(systemOverview.inbound().size(), systemOverview.outbound().size());

    var ivy = new Element(new System("Axon Ivy Engine"), middleX(), middleY(inOutMax));
    model.addElement(ivy);

    var inboundRequests = systemOverview.inbound().stream().mapToLong(channel -> channel.statistics().requests()).sum();
    var outboundRequests = systemOverview.outbound().stream().mapToLong(channel -> channel.statistics().requests()).sum();
    var inboundAverage = systemOverview.inbound().stream().mapToLong(channel -> channel.statistics().average()).sum();
    var outboundAverage = systemOverview.outbound().stream().mapToLong(channel -> channel.statistics().average()).sum();
    var requests = inboundRequests + outboundRequests;
    systemOverview.inbound().forEach(channel -> addInbound(channel, ivy, requests, inboundAverage));
    systemOverview.outbound().forEach(channel -> addOutbound(channel, ivy, requests, outboundAverage));
  }

  public void clear() {
    Tracer.instance().systemOverview().clear();
    refresh();
  }

  public boolean isNotClearable() {
    return model.getElements().size() == 1;
  }

  public void resize(ResizeEvent event) {
    width = event.getWidth();
    height = event.getHeight();
    refresh();
  }

  private void addInbound(CommunicationChannel channel, Element ivy, long requests, long average) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.LEFT).count();
    var inbound = new Element(new System(channel.systemLink()), leftX(), gridY(count));
    setTitle(inbound, channel);
    setStyleClass(inbound, channel);
    model.addElement(inbound);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    inbound.addEndPoint(out);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    ivy.addEndPoint(in);
    var strokeWidth = (int)Math.max(1, channel.statistics().requests() * 20 / requests);
    var color = color(channel.statistics().average(), average);
    connect(in, out, channel, strokeWidth, color);
  }

  private void addOutbound(CommunicationChannel channel, Element ivy, long requests, long average) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.RIGHT).count();
    var outbound = new Element(new System(channel.systemLink()), rightX(),  gridY(count));
    setTitle(outbound, channel);
    setStyleClass(outbound, channel);
    model.addElement(outbound);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    outbound.addEndPoint(in);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    ivy.addEndPoint(out);
    var strokeWidth = (int)Math.max(1, channel.statistics().requests() * 20 / requests);
    var color = color(channel.statistics().average(), average);
    connect(in, out, channel, strokeWidth, color);
  }

  private String leftX() {
    return BORDER + "px";
  }

  private String rightX() {
    return (width - ELEMENT_WIDTH - BORDER) +"px";
  }

  private String gridY(long count) {
    return (BORDER + step(count)) + "px";
  }

  private String middleY(long max) {
    if (max > 1) {
      return (BORDER + (step(max) / 2) - ELEMENT_HEIGHT / 2) + "px";
    }
    return BORDER + "px";
  }

  private long step(long max) {
    return max * (ELEMENT_HEIGHT + BORDER);
  }

  private String middleX() {
    return (width/2 - ELEMENT_WIDTH / 2)  + "px";
  }

  private void setTitle(Element element, CommunicationChannel channel) {
    var statistics = channel.statistics();
    StringBuilder builder = new StringBuilder();
    builder.append("Requests: ");
    builder.append(statistics.requests());
    builder.append("\n");
    builder.append("Errors: ");
    builder.append(statistics.errors());
    builder.append("\n");
    builder.append("Response Times: ");
    builder.append("\n");
    builder.append("Avarage: ");
    var avg = format(channel.statistics().average());
    builder.append(avg);
    builder.append("\n");
    builder.append("Minimum: ");
    var min = format(channel.statistics().minimum());
    builder.append(min);
    builder.append("\n");
    builder.append("Maximum: ");
    var max = format(channel.statistics().maximum());
    builder.append(max);
    element.setTitle(builder.toString());
  }

  private void setStyleClass(Element element, CommunicationChannel channel) {
    if (channel.statistics().errors() > 0) {
      element.setStyleClass("error");
    } else {
      element.setStyleClass("ok");
    }
  }

  private String color(long value, long max) {
    var percentage = value * 100.0f / max;
    var color = 120 - percentage * 120 / 100;
    return "hsl("+color+", 100%, 70%)";

  }

  private void connect(EndPoint in, EndPoint out, CommunicationChannel channel, int strokeWidth, String color) {
    var curviness = Math.max(1, 100+(width-1000)/2);
    var connector = new BezierConnector(curviness, -1);
    connector.setPaintStyle("{stroke:'"+color+"', strokeWidth:"+strokeWidth+"}");
    var connection = new Connection(out, in, connector);

    var avg = format(channel.statistics().average());
    var label = new LabelOverlay(channel.statistics().requests() +" requests / " + channel.statistics().errors() +" errors / " + avg);
    connection.getOverlays().add(label);
    var arrow = new ArrowOverlay(strokeWidth*2+20, strokeWidth+20, 1, 1);
    connection.getOverlays().add(arrow);
    model.connect(connection);
  }

  private String format(long value) {
    var unit = Unit.NANO_SECONDS;
    var scaledValue = Unit.NANO_SECONDS.convertTo(value, unit);
    while (scaledValue > 10000) {
      unit= unit.scaleUp();
      scaledValue = Unit.NANO_SECONDS.convertTo(value, unit);
    }
    return scaledValue+" "+ unit.symbol();
  }

  public DiagramModel getModel() {
    return model;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public static final class System {

    private final String name;

    public System(String name) {
      this.name = name;
    }

    public System(SystemLink link) {
      this(link.name()+"<br>"+link.host()+"<br>"+link.protocol()+(link.port() == -1 ? "" : " ["+link.port()+"]"));
    }

    public String getName() {
      return name;
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      System other = (System) obj;
      return Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
      return "System [name="+name+"]";
    }
  }
}
