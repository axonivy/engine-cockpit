package ch.ivyteam.enginecockpit.security.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.language.LanguageRepository;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.IWorkflowManager;
import ch.ivyteam.ivy.workflow.query.CaseQuery;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class SecurityWorkflowLanguageBean {

  private String name;

  private SecuritySystem securitySystem;
  private ManagerBean managerBean;

  private LanguageRepository languages;
  private Locale editLanguage;

  public SecurityWorkflowLanguageBean() {
    managerBean = ManagerBean.instance();
  }

  public SecurityWorkflowLanguageBean(String secSystemName) {
    this();
    setSecuritySystemName(secSystemName);
  }

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      loadSecuritySystem();
    }
  }

  private void loadSecuritySystem() {
    securitySystem = managerBean.getSecuritySystems().stream()
            .filter(system -> StringUtils.equals(system.getSecuritySystemName(), name))
            .findAny()
            .orElseThrow();
    languages = LanguageManager.instance().languages(securitySystem.getSecurityContext());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Locale> getLanguages() {
    List<Locale> langs = new ArrayList<>(languages.allWorkflow());
    Collections.sort(langs, this::compare);
    return langs;
  }

  public Set<Locale> getAddable() {
    Set<Locale> addable = new HashSet<Locale>(languages.allContent());
    addable.removeAll(languages.allWorkflow());
    return addable;
  }

  public boolean isDefault(Locale language) {
    return languages.defaultWorkflow().equals(language);
  }

  public void setDefault(Locale language) {
    languages.defaultWorkflow(language);
  }

  public Locale getDefault() {
    return languages.defaultWorkflow();
  }

  public void delete(Locale language) {
    languages.deleteWorkflow(language);
    this.editLanguage = null;
  }

  public void add(Locale language) {
    languages.createWorkflow(language);
    this.editLanguage = null;
  }

  public Locale getEditLanguage() {
    return editLanguage;
  }

  public void setEditLanguage(Locale language) {
    this.editLanguage = language;
  }

  public long getTasks() {
    var workflowContext = getWorkflowContext();
    return TaskQuery.create(workflowContext.getTaskQueryExecutor()).executor().count();
  }

  public long getCases() {
    var workflowContext = getWorkflowContext();
    return CaseQuery.create(workflowContext.getCaseQueryExecutor()).executor().count();
  }

  private IWorkflowContext getWorkflowContext() {
    return IWorkflowManager.instance().getWorkflowContext(securitySystem.getSecurityContext());
  }

  private int compare(Locale l1, Locale l2) {
    return l1.getDisplayName().compareTo(l2.getDisplayName());
  }
}
