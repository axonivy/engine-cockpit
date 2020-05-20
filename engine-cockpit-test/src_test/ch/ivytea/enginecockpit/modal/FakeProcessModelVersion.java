package ch.ivytea.enginecockpit.modal;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IProject;

import ch.ivyteam.ivy.application.ActivityOperationState;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ProcessModelVersionRelation;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.RuntimeLogCategory;
import ch.ivyteam.ivy.application.value.QualifiedVersion;
import ch.ivyteam.log.Logger;

class FakeProcessModelVersion implements IProcessModelVersion
{

  @Override
  public ActivityState getActivityState()
  {
    return null;
  }

  @Override
  public ActivityOperationState getActivityOperationState()
  {
    return null;
  }

  @Override
  public void activate()
  {
  }

  @Override
  public void deactivate()
  {
  }

  @Override
  public void lock()
  {
  }

  @Override
  public <T> T getAdapter(Class<T> adapter)
  {
    return null;
  }

  @Override
  public String getName()
  {
    return null;
  }

  @Override
  public String getVersionName()
  {
    return null;
  }

  @Override
  public String getDescription()
  {
    return null;
  }

  @Override
  public int getVersionNumber()
  {
    return 0;
  }

  @Override
  public ReleaseState getReleaseState()
  {
    return null;
  }

  @Override
  public Date getReleaseTimestamp()
  {
    return null;
  }

  @Override
  public Date getScheduledReleaseTimestamp()
  {
    return null;
  }

  @Override
  public Date getLastChangeDate()
  {
    return new Date();
  }

  @Override
  public String getLastChangeBy()
  {
    return null;
  }

  @Override
  public String getLastChangeFromHost()
  {
    return null;
  }

  @Override
  public IProcessModel getProcessModel()
  {
    return null;
  }

  @Override
  public IApplication getApplication()
  {
    return null;
  }

  @Override
  public void release()
  {
  }

  @Override
  public void release(Date dateTimeOfRelease)
  {
  }

  @Override
  public void delete()
  {
  }

  @Override
  public boolean isRequired()
  {
    return false;
  }

  @Override
  public String getProjectDirectory()
  {
    return null;
  }

  @Override
  public URI getProjectUri()
  {
    return null;
  }

  @Override
  public long getId()
  {
    return 0;
  }

  @Override
  public int getIdentifier()
  {
    return 0;
  }

  @Override
  public ActivityState getInheritedActivityState()
  {
    return null;
  }

  @Override
  public Logger getRuntimeLog(RuntimeLogCategory category)
  {
    return null;
  }

  @Override
  public boolean isSystem()
  {
    return false;
  }

  @Override
  public IProject getProject()
  {
    return null;
  }

  @Override
  public String getProjectName()
  {
    return null;
  }

  @Override
  public ILibrary getLibrary()
  {
    return null;
  }

  @Override
  public ILibrary createLibrary(String id, int majorVersion)
  {
    return null;
  }

  @Override
  public ILibrary createLibrary(String id, QualifiedVersion version)
  {
    return null;
  }

  @Override
  public void deleteLibrary()
  {
  }

  @Override
  public void activateRequiredProcessModelVersions()
  {
  }

  @Override
  public void deactivateDependentProcessModelVersions()
  {
  }

  @Override
  public List<IProcessModelVersion> getAllRelatedProcessModelVersions(ProcessModelVersionRelation relation)
  {
    return null;
  }

}
