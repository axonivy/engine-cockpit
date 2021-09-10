package ch.ivyteam.enginecockpit.security;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.enginecockpit.ManagerBean;

@Named
@FacesConverter(value = "userConverter")
public class UserConverter implements Converter {

  @Override
  public User getAsObject(FacesContext context, UIComponent component, String value) {
    if (StringUtils.isNotBlank(value)) {
      var userId = Long.parseLong(value);
      var managebean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",ManagerBean.class);
      var user = managebean.getSelectedIApplication().getSecurityContext().findUser(userId);
      if (user != null) {
        return new User(user);
      }
    }
    return null;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof User) {
      var user = (User) value;
      return String.valueOf(user.getId());
    }
    return null;
  }
}
