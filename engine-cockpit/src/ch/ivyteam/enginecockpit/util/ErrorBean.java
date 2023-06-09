package ch.ivyteam.enginecockpit.util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class ErrorBean {

  private ErrorValue error;

  public ErrorValue getSelected() {
    Ivy.log().info("<"+error);
    return error;
  }

  public void setSelected(ErrorValue error) {
    Ivy.log().info(">"+error);
    this.error = error;
  }
}
