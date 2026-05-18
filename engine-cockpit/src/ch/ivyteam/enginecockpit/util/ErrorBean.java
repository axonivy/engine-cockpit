package ch.ivyteam.enginecockpit.util;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

@Named
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
