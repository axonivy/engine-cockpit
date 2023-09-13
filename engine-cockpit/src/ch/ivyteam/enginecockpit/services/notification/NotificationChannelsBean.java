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

  private List<NotificationChannelDto> channels;
  private List<NotificationChannelDto> filteredChannels;
  private String filter;

  private ManagerBean managerBean;

  public NotificationChannelsBean() {
    managerBean = ManagerBean.instance();
  }

  public void onload() {
    channels = NotificationChannel.all().stream()
            .map(channel -> NotificationChannelDto.create(managerBean, channel))
            .collect(Collectors.toList());
  }

  public List<NotificationChannelDto> getChannels() {
    return channels;
  }

  public List<NotificationChannelDto> getFilteredChannels() {
    return filteredChannels;
  }

  public void setFilteredChannels(List<NotificationChannelDto> filteredChannels) {
    this.filteredChannels = filteredChannels;
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
