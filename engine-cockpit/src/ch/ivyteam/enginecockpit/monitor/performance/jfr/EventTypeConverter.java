package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import jdk.jfr.EventType;

@FacesConverter("EventTypeConverter")
public class EventTypeConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) throws ConverterException {
    return null;
  }

  @Override
  public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
    return ((EventType) arg2).getName();
  }

}
