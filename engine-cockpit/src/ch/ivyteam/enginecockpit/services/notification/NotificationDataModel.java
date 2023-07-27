package ch.ivyteam.enginecockpit.services.notification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ch.ivyteam.ivy.jsf.primefaces.sort.SortMetaConverter;
import ch.ivyteam.ivy.notification.query.NotificationDeliveryQuery;

public class NotificationDataModel extends LazyDataModel<NotificationDeliveryDto> {

  private NotificationDto notification;
  private String filter;

  public NotificationDataModel(NotificationDto notification) {
    this.notification = notification;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  @Override
  public List<NotificationDeliveryDto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var query = query();

    applyFilter(query);
    applyOrdering(query, sortBy);

    var deliveries = query
            .executor()
            .results(first, pageSize).stream()
            .map(NotificationDeliveryDto::new)
            .collect(Collectors.toList());
    setRowCount((int) query.executor().count());
    return deliveries;
  }

  private void applyFilter(NotificationDeliveryQuery query) {
    if (StringUtils.isNotEmpty(filter)) {
      var dbFilter = "%" + filter + "%";
      query.where().and(query().where()
             .channel().isLikeIgnoreCase(dbFilter))
          .or()
            .notificationDeliveryId().isLikeIgnoreCase(dbFilter);
    }
  }

  private NotificationDeliveryQuery query() {
    return notification.getDeliveries().query();
  }

  private static void applyOrdering(NotificationDeliveryQuery query, Map<String, SortMeta> sortBy) {
    var sort = new SortMetaConverter(sortBy);
    var sortOrder = sort.toOrder();
    var sortField = sort.toField();

    if (StringUtils.isEmpty(sortField)) {
      return;
    }
    if ("channel".equals(sortField)) {
      applyOrdering(query.orderBy().channel(), sortOrder);
    }
    if ("deliveredAt".equals(sortField)) {
      applyOrdering(query.orderBy().deliveredAt(), sortOrder);
    }
    if ("readAt".equals(sortField)) {
      applyOrdering(query.orderBy().readAt(), sortOrder);
    }
  }

  private static void applyOrdering(NotificationDeliveryQuery.OrderByColumnQuery query, SortOrder sortOrder) {
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
