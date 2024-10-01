package ch.ivyteam.enginecockpit.monitor.session;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISessionInternal;

public class SessionDataModel extends LazyDataModel<SessionDto> {

  private static final List<Function<ISessionInternal, String>> GLOBAL_FILTERS =  List.of(
          s -> s.getSessionUser() == null ? "" : s.getSessionUser().getName(),
          ISessionInternal::creationReason,
          ISessionInternal::getAuthenticationMode,
          s -> Integer.toString(s.getIdentifier()),
          s -> s.getSecurityContext().getName()
  );

  private static final Map<String, Function<ISessionInternal, String>> SORTS_STRING =  Map.of(
          "user", s -> s.getSessionUser() == null ? "" : s.getSessionUser().getName(),
          "securitySystem", s -> s.getSecurityContext().getName(),
          "cause", s -> StringUtils.defaultString(s.creationReason()),
          "authMode", s -> StringUtils.defaultString(s.getAuthenticationMode())
  );
  private static final Map<String, Function<ISessionInternal, Instant>> SORTS_DATE =  Map.of(
          "createdAt", ISessionInternal::createdAt,
          "lastAccessedAt", ISessionInternal::lastAccessedAt
  );
  private static final Map<String, Function<ISessionInternal, Integer>> SORTS_NUMBER =  Map.of(
          "httpSessionCount", s -> s.getHttpSessions().size(),
          "id", ISessionInternal::getIdentifier
  );

  private boolean showUnauthenticatedSessions;
  private String filter;

  public SessionDataModel() {
    resetFilters();
  }

  public void resetFilters() {
    this.showUnauthenticatedSessions = true;
    this.filter = null;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public boolean isShowUnauthenticatedSessions() {
    return showUnauthenticatedSessions;
  }

  public void setShowUnauthenticatedSessions(boolean showUnauthenticatedSessions) {
    this.showUnauthenticatedSessions = showUnauthenticatedSessions;
  }

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    return (int) sessions()
            .filter(this::filter)
            .count();
  }

  @Override
  public List<SessionDto> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var stream = sessions().filter(this::filter);
    var comparator = createComparator(sortBy);
    if (comparator != null) {
      stream = stream.sorted(comparator);
    }
    return stream
            .skip(first)
            .limit(pageSize)
            .map(SessionDto::new)
            .collect(Collectors.toList());
  }

  private Comparator<ISessionInternal> createComparator(Map<String, SortMeta> sortBy) {
    Comparator<ISessionInternal> comparator = null;
    var sortKeys = sortBy.keySet();
    if (!sortKeys.isEmpty()) {
      var sortKey = sortKeys.iterator().next();

      var stringSorter = SORTS_STRING.get(sortKey);
      if (stringSorter != null) {
        comparator = Comparator.comparing(stringSorter);
      }

      var dateSorter = SORTS_DATE.get(sortKey);
      if (dateSorter != null) {
        comparator = Comparator.comparing(dateSorter);
      }
      var numberSorter = SORTS_NUMBER.get(sortKey);
      if (numberSorter != null) {
        comparator = Comparator.comparing(numberSorter);
      }

      var sortMeta = sortBy.get(sortKey);
      var order = sortMeta.getOrder();
      if (comparator != null && order.isDescending()) {
        comparator = comparator.reversed();
      }
    }
    return comparator;
  }

  private boolean filter(ISessionInternal session) {
    if (StringUtils.isBlank(filter)) {
      return true;
    }
    for (var f : GLOBAL_FILTERS) {
      if (StringUtils.containsIgnoreCase(f.apply(session), filter)) {
        return true;
      }
    }
    return false;
  }

  private Stream<ISessionInternal> sessions() {
    return ISecurityContextRepository.instance().all()
            .stream()
            .flatMap(s -> s.sessions().all().stream())
            .filter(s -> !s.isSessionUserSystemUser())
            .filter(s -> showUnauthenticatedSessions || (!showUnauthenticatedSessions && !s.isSessionUserUnknown()))
            .map(ISessionInternal.class::cast);
  }
}
