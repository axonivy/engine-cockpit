package ch.ivyteam.enginecockpit.monitor;

import java.util.UUID;

import ch.ivyteam.ivy.persistence.PersistencyException;
import ch.ivyteam.ivy.process.extension.ui.ExtensionUiBuilder;
import ch.ivyteam.ivy.process.extension.ui.IUiFieldEditor;
import ch.ivyteam.ivy.process.extension.ui.UiEditorExtension;
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

  public static class Editor extends UiEditorExtension {

    private IUiFieldEditor scriptField;

    @Override
    public void initUiFields(ExtensionUiBuilder ui) {
      scriptField = ui.scriptField().create();
    }

    @Override
    protected void loadUiDataFromConfiguration() {
      // ===> Add here your code to load data from the configuration to the ui
      // widgets
      // <===
      // You can use the getBeanConfiguration() or
      // getBeanConfigurationProperty()
      // methods to load the configuration
      scriptField.setText(getBeanConfigurationProperty("demo"));
    }

    @Override
    protected boolean saveUiDataToConfiguration() {
      // Clear the bean configuration and all its properties to flush outdated
      // configurations.
      clearBeanConfiguration();
      // ===> Add here your code to save the data in the ui widgets to the
      // configuration <===
      // You can use the setBeanConfiguration() or
      // setBeanConfigurationProperty()
      // methods to save the configuration
      setBeanConfigurationProperty("demo", scriptField.getText());
      return true;
    }
  }
}
