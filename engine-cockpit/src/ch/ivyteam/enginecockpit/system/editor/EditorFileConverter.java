package ch.ivyteam.enginecockpit.system.editor;

import java.util.NoSuchElementException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFileRepository;

@Named
@FacesConverter(value = "editorFileConverter")
public class EditorFileConverter implements Converter {

  @Override
  public EditorFile getAsObject(FacesContext context, UIComponent component, String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    try {
      return ConfigFileRepository.instance().all()
          .filter(conf -> conf.file().toString().equalsIgnoreCase(value))
          .findFirst()
          .map(EditorFile::new)
          .orElseThrow();
    } catch (NoSuchElementException e) {
      throw new ConverterException(
          new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid file."));
    }
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
    if (value instanceof EditorFile file) {
      return file.getPath().toString();
    }
    return null;
  }
}
