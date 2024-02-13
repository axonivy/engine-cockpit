package ch.ivyteam.enginecockpit.services.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.jsf.primefaces.sort.SortMetaConverter;
import ch.ivyteam.ivy.notification.channel.Event;
import ch.ivyteam.ivy.notification.impl.NotificationRepository;
import ch.ivyteam.ivy.notification.query.NotificationQuery;
import ch.ivyteam.ivy.notification.query.NotificationQuery.FilterLink;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.query.UserQuery;

public class NotificationsDataModel extends LazyDataModel<NotificationDto> {

  private SecuritySystem securitySystem;
  private String filter;

  public NotificationsDataModel(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  @Override
  public List<NotificationDto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var query = query();

    applyFilter(query);
    applyOrdering(query, sortBy);

    var notifications = query
            .executor()
            .results(first, pageSize).stream()
            .map(NotificationDto::new)
            .collect(Collectors.toList());
    setRowCount((int) query.executor().count());
    return notifications;
  }

  private void applyFilter(NotificationQuery query) {
    if (StringUtils.isNotEmpty(filter)) {
      FilterLink queryFilter = query().where().notificationId().isEqualIgnoreCase(filter)
          .or().template().isEqualIgnoreCase(filter);
      var receiverIds = matchingReceiverIds();
      for (var receiverId : receiverIds) {
        queryFilter.or().receiverId().isEqual(receiverId);
      }
      var eventKinds = matchingEventKinds();
      for (var kind : eventKinds) {
        queryFilter.or().kind().isEqual(kind);
      }
      query.where().and(queryFilter);
    }
  }

  private List<String> matchingEventKinds() {
    return Event
        .all()
        .stream()
        .filter(event -> event.displayName(Locale.ENGLISH).toLowerCase().contains(filter.toLowerCase()))
        .map(Event::kind)
        .toList();
  }

  private ArrayList<String> matchingReceiverIds() {
    var receiverIds = new ArrayList<String>();
    var matchingUsers = UserQuery.create(securitySystem.getSecurityContext().users().queryExecutor())
        .where()
        .name().isEqual(filter).or()
        .fullName().isEqual(filter)
        .executor()
        .results(0,  2);
    receiverIds.addAll(matchingUsers.stream().map(IUser::getSecurityMemberId).toList());
    var matchingRole = securitySystem.getSecurityContext().roles().find(filter);
    if (matchingRole != null) {
      receiverIds.add(matchingRole.getSecurityMemberId());
    }
    return receiverIds;
  }

  private NotificationQuery query() {
    return NotificationRepository.of(securitySystem.getSecurityContext()).query();
  }

  private static void applyOrdering(NotificationQuery query, Map<String, SortMeta> sortBy) {
    var sort = new SortMetaConverter(sortBy);
    var sortOrder = sort.toOrder();
    var sortField = sort.toField();

    if (StringUtils.isEmpty(sortField)) {
      return;
    }
    if ("createdAt".equals(sortField)) {
      applyOrdering(query.orderBy().createdAt(), sortOrder);
    }
    if ("kind".equals(sortField)) {
      applyOrdering(query.orderBy().kind(), sortOrder);
    }
  }

  private static void applyOrdering(NotificationQuery.OrderByColumnQuery query, SortOrder sortOrder) {
    if (SortOrder.ASCENDING == sortOrder) {
      query.ascending();
    }
    if (SortOrder.DESCENDING == sortOrder) {
      query.descending();
    }
  }

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    return getRowCount();
  }
}
