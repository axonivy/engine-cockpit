package ch.ivyteam.enginecockpit.monitor.system.overview;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

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
    systemOverview.inbound().forEach(channel -> addInbound(channel, ivy));
    systemOverview.outbound().forEach(channel -> addOutbound(channel, ivy));
  }

  private void addInbound(CommunicationChannel channel, Element ivy) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.LEFT).count();
    var inbound = new Element(new System(channel.systemLink()), "1em", (1+count*8)+"em");
    model.addElement(inbound);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    inbound.addEndPoint(out);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    ivy.addEndPoint(in);
    model.connect(new Connection(out, in));
    connect(in, out, channel);
  }

  private void addOutbound(CommunicationChannel channel, Element ivy) {
    var count = ivy.getEndPoints().stream().filter(ep -> ep.getAnchor() == EndPointAnchor.RIGHT).count();
    var outbound = new Element(new System(channel.systemLink()), "98em",  (1+count*8)+"em");
    model.addElement(outbound);
    var in = new BlankEndPoint(EndPointAnchor.LEFT);
    outbound.addEndPoint(in);
    var out = new BlankEndPoint(EndPointAnchor.RIGHT);
    ivy.addEndPoint(out);
    connect(in, out, channel);
  }

  private void connect(EndPoint in, EndPoint out, CommunicationChannel channel) {
    var connection = new Connection(out, in);
    var label = new LabelOverlay(channel.requests() +" requests /" + channel.average() +" [ns]", "flow-label", 0.5);
    connection.getOverlays().add(label);
    var arrow = new ArrowOverlay(20, 20, 1, 1);
    connection.getOverlays().add(arrow);
    model.connect(connection);
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
