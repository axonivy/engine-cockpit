package ch.ivyteam.enginecockpit.monitor.blob;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.blob.storage.core.impl.BlobRepository;
import ch.ivyteam.ivy.blob.storage.core.query.BlobQuery;
import ch.ivyteam.ivy.jsf.primefaces.sort.SortMetaConverter;

public class BlobsDataModel extends LazyDataModel<BlobDto> {

  private final SecuritySystem securitySystem;
  private String filter;

  public BlobsDataModel(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  @Override
  public List<BlobDto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var query = query();

    applyFilter(query);
    applyOrdering(query, sortBy);

    var blobs = query
        .executor()
        .results(first, pageSize).stream()
        .map(BlobDto::new)
        .collect(Collectors.toList());
    setRowCount((int) query.executor().count());
    return blobs;
  }

  private void applyFilter(BlobQuery query) {
    if (StringUtils.isNotEmpty(filter)) {
      var dbFilter = "%" + filter + "%";
      query.where().and(query().where()
          .path().isLikeIgnoreCase(dbFilter))
          .or()
          .uUID().isEqual(dbFilter);
    }
  }

  private BlobQuery query() {
    return BlobRepository.of(securitySystem.getSecurityContext()).query();
  }

  private static void applyOrdering(BlobQuery query, Map<String, SortMeta> sortBy) {
    var sort = new SortMetaConverter(sortBy);
    var sortOrder = sort.toOrder();
    var sortField = sort.toField();

    if (StringUtils.isEmpty(sortField)) {
      return;
    }
    if ("createdAt".equals(sortField)) {
      applyOrdering(query.orderBy().createdAt(), sortOrder);
    }
    if ("modifiedAt".equals(sortField)) {
      applyOrdering(query.orderBy().modifiedAt(), sortOrder);
    }
    if ("path".equals(sortField)) {
      applyOrdering(query.orderBy().path(), sortOrder);
    }
    if ("size".equals(sortField)) {
      applyOrdering(query.orderBy().size(), sortOrder);
    }
  }

  private static void applyOrdering(BlobQuery.OrderByColumnQuery query, SortOrder sortOrder) {
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
