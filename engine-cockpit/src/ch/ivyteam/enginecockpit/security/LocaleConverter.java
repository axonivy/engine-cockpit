package ch.ivyteam.enginecockpit.security;

import java.util.Locale;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

import org.apache.commons.lang3.LocaleUtils;

@FacesConverter("localeConverter")
public class LocaleConverter implements Converter<Object> {

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
