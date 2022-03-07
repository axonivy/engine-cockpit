package ch.ivyteam.enginecockpit.monitor.system.overview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

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
public class SystemOverviewBean {

  private DefaultDiagramModel model;

  public SystemOverviewBean() {
    model = new DefaultDiagramModel();
    model.setMaxConnections(-1);
    model.setContainment(false);

    var ivy = new Element(new System("Axon Ivy Engine"), "50em", "15em");
    model.addElement(ivy);

    var systemOverview = Tracer.instance().systemOverview();
    var inboundRequests = systemOverview.inbound().stream().mapToLong(channel -> channel.statistics().requests()).sum();
    var outboundRequests = systemOverview.outbound().stream().mapToLong(channel -> channel.statistics().requests()).sum();
    var inboundAverage = systemOverview.inbound().stream().mapToLong(channel -> channel.statistics().average()).sum();
    var outboundAverage = systemOverview.outbound().stream().mapToLong(channel -> channel.statistics().average()).sum();
    var requests = inboundRequests + outboundRequests;
    systemOverview.inbound().forEach(channel -> addInbound(channel, ivy, requests, inboundAverage));
    systemOverview.outbound().forEach(channel -> addOutbound(channel, ivy, requests, outboundAverage));
  }

  private void addInbound(CommunicationChannel channel, Element ivy, long requests, long average) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.LEFT).count();
    var inbound = new Element(new System(channel.systemLink()), "1em", (1+count*8)+"em");
    model.addElement(inbound);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    inbound.addEndPoint(out);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    ivy.addEndPoint(in);
    var width = (int)Math.max(1, channel.statistics().requests() * 20 / requests);
    var color = color(channel.statistics().average(), average);
    connect(in, out, channel, width, color);
  }

  private void addOutbound(CommunicationChannel channel, Element ivy, long requests, long average) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.RIGHT).count();
    var outbound = new Element(new System(channel.systemLink()), "98em",  (1+count*8)+"em");
    model.addElement(outbound);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    outbound.addEndPoint(in);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    ivy.addEndPoint(out);
    var width = (int)Math.max(1, channel.statistics().requests() * 20 / requests);
    var color = color(channel.statistics().average(), average);
    connect(in, out, channel, width, color);
  }

  private String color(long value, long max) {
    var percentage = value * 100.0f / max;
    var color = 120 - percentage * 120 / 100;
    return "hsl("+color+", 100%, 70%)";

  }

  private void connect(EndPoint in, EndPoint out, CommunicationChannel channel, int width, String color) {
    var connector = new BezierConnector(400, -1);
    connector.setPaintStyle("{strokeStyle:'"+color+"', lineWidth:"+width+"}");
    var connection = new Connection(out, in, connector);

    var avg = format(channel.statistics().average());
    var min = format(channel.statistics().minimum());
    var max = format(channel.statistics().maximum());
    var label = new LabelOverlay(channel.statistics().requests() +" requests / " + avg +" ("+min+" .. "+max+")", "flow-label", 0.5);
    connection.getOverlays().add(label);
    var arrow = new ArrowOverlay(width*2+20, width+20, 1, 1);
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
  }
}
