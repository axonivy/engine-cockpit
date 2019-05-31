package ch.ivyteam.enginecockpit.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang.text.StrSubstitutor;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.ExternalDatabase;
import ch.ivyteam.ivy.db.IExternalDatabase;
import ch.ivyteam.ivy.db.internal.ExternalDatabaseManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class ExternalDatabaseDetailBean extends HelpServices
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
    return "External Database '" + databaseName + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your External Database overwrite your app.yaml file. For example copy and paste the snippet below.";
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
  
  public void testDbConnection()
  {
    FacesMessage message;
    ExternalDatabaseManager dbManager = DiCore.getGlobalInjector().getInstance(ExternalDatabaseManager.class);
    IExternalDatabase iExternalDatabase = dbManager.getExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    try
    {
      if (iExternalDatabase.getConnection().isValid(10))
      {
        message = new FacesMessage("Successful connected to database", "");
      }
      else
      {
        message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Failed", "Could not connect to database");
      }
    }
    catch (SQLException ex)
    {
      message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg", message);
  }

}
