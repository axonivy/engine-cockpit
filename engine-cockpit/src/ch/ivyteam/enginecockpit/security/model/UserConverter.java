package ch.ivyteam.enginecockpit.security.model;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@FacesConverter(value = "userConverter")
public class UserConverter implements Converter<Object> {

  @Override
  public User getAsObject(FacesContext context, UIComponent component, String value) {
    if (StringUtils.isNotBlank(value)) {
      var managebean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
          ManagerBean.class);
      var user = managebean.getSelectedSecuritySystem().getSecurityContext().users().query().where()
          .securityMemberId().isEqual(value).executor().firstResult();
      if (user != null) {
        return new User(user);
      }
    }
    return null;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof User user) {
      return user.getSecurityMemberId();
    }
    return null;
  }
}
