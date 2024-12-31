package ch.ivyteam.enginecockpit.security.model;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@FacesConverter(value = "userConverter")
public class UserConverter implements Converter {

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
