
package ch.ivyteam.enginecockpit.monitor;

import java.util.UUID;

import ch.ivyteam.ivy.persistence.PersistencyException;
import ch.ivyteam.ivy.process.intermediateevent.AbstractProcessIntermediateEventBean;

public class TestIntermediateClass extends AbstractProcessIntermediateEventBean {

  public TestIntermediateClass() {
    super("TestIntermediateClass", "Description of TestIntermediateClass", String.class);
  }

  @Override
  public void poll() {
    var additionalInformation = "";
    var resultObject = "";
    var eventIdentifier = UUID.randomUUID().toString();
    try {
      getEventBeanRuntime().fireProcessIntermediateEventEx(eventIdentifier, resultObject, additionalInformation);
    } catch (PersistencyException ex) {
      throw new RuntimeException(ex);
    }
  }
}
