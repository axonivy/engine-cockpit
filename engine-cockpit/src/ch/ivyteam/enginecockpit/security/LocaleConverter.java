package ch.ivyteam.enginecockpit.security;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.apache.commons.lang3.LocaleUtils;

@FacesConverter("localeConverter")
public class LocaleConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String locale) throws ConverterException {
    return LocaleUtils.toLocale(locale);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object locale) throws ConverterException {
    if (locale == null) {
      return "";
    }
    return ((Locale) locale).toString();
  }
}
