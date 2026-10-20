package ch.ivyteam.enginecockpit.monitor.blob;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@ViewScoped
public class BlobsBean implements Serializable {

  @Inject
  private ManagerBean managerBean;

  private BlobsDataModel dataModel;

  public BlobsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new BlobsDataModel(managerBean.getSelectedSecuritySystem());
  }
}
