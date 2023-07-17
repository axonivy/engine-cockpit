package ch.ivyteam.enginecockpit.services.notification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.jsf.primefaces.sort.SortMetaConverter;
import ch.ivyteam.ivy.notification.NotificationRepository;
import ch.ivyteam.ivy.notification.query.NotificationQuery;

public class NotificationDataModel extends LazyDataModel<Notification> {

  private SecuritySystem securitySystem;
  private String filter;

  public NotificationDataModel(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
  }

  public void setSecuritySystem(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  @Override
  public List<Notification> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var query = query();

    applyFilter(query);
    applyOrdering(query, sortBy);

    var notifications = query
            .executor()
            .results(first, pageSize).stream()
            .map(Notification::new)
            .collect(Collectors.toList());
    setRowCount((int) query.executor().count());
    return notifications;
  }

  private void applyFilter(NotificationQuery query) {
    if (StringUtils.isNotEmpty(filter)) {
      var dbFilter = "%" + filter + "%";
      query.where().and(query().where().kind().isLikeIgnoreCase(dbFilter));
    }
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
    if ("receiver".equals(sortField)) {
      applyOrdering(query.orderBy().receiverId(), sortOrder);
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
