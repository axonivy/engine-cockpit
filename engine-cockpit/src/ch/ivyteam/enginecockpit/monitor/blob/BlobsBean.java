package ch.ivyteam.enginecockpit.monitor.blob;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@ViewScoped
public class BlobsBean implements Serializable {

  private BlobsDataModel dataModel;

  public BlobsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new BlobsDataModel(ManagerBean.instance().getSelectedSecuritySystem());
  }
}
