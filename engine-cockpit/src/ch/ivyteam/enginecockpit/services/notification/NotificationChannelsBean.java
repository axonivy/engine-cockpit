package ch.ivyteam.enginecockpit.services.notification;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;

@ManagedBean
@ViewScoped
public class NotificationChannelsBean {

  private List<NotificationChannelConfigDto> configs;
  private List<NotificationChannelConfigDto> filteredConfigs;
  private String filter;

  private ManagerBean managerBean;

  public NotificationChannelsBean() {
    managerBean = ManagerBean.instance();
  }

  public void onload() {
    configs = NotificationChannel.all().stream()
            .map(channel -> NotificationChannelConfigDto.create(managerBean, channel))
            .collect(Collectors.toList());
  }

  public List<NotificationChannelConfigDto> getConfigs() {
    return configs;
  }

  public List<NotificationChannelConfigDto> getFilteredConfigs() {
    return filteredConfigs;
  }

  public void setFilteredConfigs(List<NotificationChannelConfigDto> filteredConfigs) {
    this.filteredConfigs = filteredConfigs;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public ManagerBean getManagerBean() {
    return managerBean;
  }

  public void setManagerBean(ManagerBean managerBean) {
    this.managerBean = managerBean;
  }
}
