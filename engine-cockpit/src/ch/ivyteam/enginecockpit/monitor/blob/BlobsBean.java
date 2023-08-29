package ch.ivyteam.enginecockpit.monitor.blob;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class BlobsBean {

  private BlobsDataModel dataModel;

  public BlobsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new BlobsDataModel(ManagerBean.instance().getSelectedSecuritySystem());
  }
}
