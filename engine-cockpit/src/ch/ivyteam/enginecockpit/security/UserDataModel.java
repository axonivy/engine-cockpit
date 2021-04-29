package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.SessionInfo;
import ch.ivyteam.ivy.security.query.UserQuery;

public class UserDataModel extends LazyDataModel<User>
{
  private IApplication app;
  private IRole filterRole;
  private String filter;

  public void setApp(IApplication app)
  {
    this.app = app;
  }

  public void setFilterRole(IRole filterRole)
  {
    this.filterRole = filterRole;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;    
  }

  public String getFilter()
  {
    return filter;
  }

  @Override
  public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters)
  {
    var userQuery = userQuery();

    filterRole(userQuery);
    applyFilter(userQuery);
    applyOrdering(userQuery, sortField, sortOrder);

    var users = userQuery
        .executor()
        .resultsPaged()
        .map(User::new)
        .window(first, pageSize);
    checkIfUserIsLoggedIn(app, users);
    setRowCount((int) userQuery.executor().count());
    return users;
  }

  private void filterRole(UserQuery userQuery)
  {
    if (filterRole != null)
    {
      userQuery.where().hasRole(filterRole);
    }
  }

  private void applyFilter(UserQuery query)
  {
    if (StringUtils.isNotEmpty(filter))
    {
      query.where().and(userQuery().where()
        .name().isLikeIgnoreCase(filter + "%")
       .or()
        .fullName().isLikeIgnoreCase(filter + "%")
       .or()
        .eMailAddress().isLikeIgnoreCase(filter + "%"));
    }
  }
  
  private UserQuery userQuery()
  {
    return app.getSecurityContext().users().query();
  }

  private static void applyOrdering(UserQuery query, String sortField, SortOrder sortOrder)
  {
    if (StringUtils.isEmpty(sortField))
    {
      return;
    }
    if ("name".equals(sortField))
    {
      applySorting(query.orderBy().name(), sortOrder);
    }
    if ("fullName".equals(sortField))
    {
      applySorting(query.orderBy().fullName(), sortOrder);
    }
    if ("email".equals(sortField))
    {
      applySorting(query.orderBy().eMailAddress(), sortOrder);
    }
  }
  
  private static void applySorting(UserQuery.OrderByColumnQuery query, SortOrder sortOrder)
  {
    if (SortOrder.ASCENDING.equals(sortOrder))
    {
      query.ascending();
    }
    if (SortOrder.DESCENDING.equals(sortOrder))
    {
      query.descending();
    }
  }

  private static void checkIfUserIsLoggedIn(IApplication app, List<User> appUsers)
  {
    for (SessionInfo session : app.getSecurityContext().getClusterSessionsSnapshot().getSessionInfos())
    {
      String sessionUser = session.getSessionUserName();
      appUsers.stream()
        .filter(u -> u.getName().equals(sessionUser))
        .findAny()
        .ifPresent(user -> user.setLoggedIn(true));
    }
  }
}
