package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.ExternalDatabase;

@ManagedBean
@ViewScoped
public class ExternalDatabaseDetailBean extends EditServices
{
  private ExternalDatabase externalDatabase;
  private String databaseName;
  
  private ManagerBean managerBean;
  
  public ExternalDatabaseDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getDatabaseName()
  {
    return databaseName;
  }
  
  public void setDatabaseName(String databaseName)
  {
    this.databaseName = databaseName;
    externalDatabase = new ExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
  }
  
  public ExternalDatabase getExternalDatabase()
  {
    return externalDatabase;
  }

  @Override
  public String getTitle()
  {
    return "Edit External Database '" + databaseName + "'";
  }
  
  @Override
  public String getYaml()
  {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("name", externalDatabase.getName());
    valuesMap.put("url", externalDatabase.getUrl());
    valuesMap.put("driver", externalDatabase.getDriver());
    valuesMap.put("username", externalDatabase.getUserName());
    valuesMap.put("maxConnections", String.valueOf(externalDatabase.getMaxConnections()));
    valuesMap.put("properties", parsePropertiesToYaml(externalDatabase.getProperties()));
    String templateString = readTemplateString("externaldatabase.yaml");
    StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

}
