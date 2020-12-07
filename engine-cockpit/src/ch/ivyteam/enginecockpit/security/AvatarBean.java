package ch.ivyteam.enginecockpit.security;

import java.io.IOException;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.avatar.IAvatar;

@ManagedBean
@SessionScoped
public class AvatarBean
{
  private String userName;
  private String roleName;
  private String applicationName;
  private ManagerBean managerBean;  
  
  private CroppedImage croppedImage;
  private String newImageName;
  
  public AvatarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
  }

  public void handleFileUpload(FileUploadEvent event) 
  {
    var uploadedFile = event.getFile();
    IAvatar avatar = getAvatar();
    FacesMessage msg = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
    try (var is = uploadedFile.getInputstream())
    {
      avatar.save(uploadedFile.getInputstream());
    }
    catch(IOException ex)
    {
      Ivy.log().error("Could not save avatar", ex);
      msg = new FacesMessage("Could not save avatar", ex.getMessage());
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
    }
    FacesContext.getCurrentInstance().addMessage(null, msg);
}

  private IAvatar getAvatar()
  {
    ISecurityContext securityContext = managerBean.getSelectedIApplication().getSecurityContext();
    if (userName != null)
    {
      return securityContext.users().find(userName).avatar();
    }
    else
    {
      return securityContext.findRole(roleName).avatar();
    }
  }

  public String getUserName()
  {
    return userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
    this.roleName = null;
  }
  
  public String getRoleName()
  {
    return roleName;
  }
  
  public void setRoleName(String roleName)
  {
    this.roleName = roleName;
    this.userName = null;
  }
  
  public String getApplicationName()
  {
    return applicationName;
  }
  
  public void setApplicationName(String applicationName)
  {
    this.applicationName = applicationName;
  }
  
  public String getOriginalImageUrl()
  {
    if (userName != null)
    {
      return "http://localhost:8081/"+getApplicationName()+"/avatar/users/"+userName+"?original&rnd="+new Random().nextInt();
    }
    else
    {
      return "http://localhost:8081/"+getApplicationName()+"/avatar/roles/"+roleName+"?original&rnd="+new Random().nextInt();
    }
  }
  
  public String getOriginalCroppedImageUrl()
  {
    return getOriginalImageUrl()+"&crop";
  }
  
  public CroppedImage getCroppedImage()
  {
      return croppedImage;
  }

  public void setCroppedImage(CroppedImage croppedImage) 
  {
      this.croppedImage = croppedImage;
  }

  public void crop() 
  {
    getAvatar().crop(croppedImage.getLeft(), croppedImage.getTop(), croppedImage.getWidth(), croppedImage.getHeight());
  }
   
   
  public String getNewImageName() {
      return newImageName;
  }

  public void setNewImageName(String newImageName) {
      this.newImageName = newImageName;
  }
}
