package ch.ivyteam.enginecockpit.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ErrorBean {

  private ErrorValue error;

  public ErrorValue getSelected() {
    return error;
  }

  public void setSelected(ErrorValue error) {
    this.error = error;
  }
}
