package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Level;

import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.synch.SynchronizationListener;
import ch.ivyteam.ivy.security.synch.UpdateEvent;

@ManagedBean
@ViewScoped
public class SecurityBean {
	private List<SecuritySystem> systems;
	
	private ApplicationBean applicationBean;
	
	public SecurityBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
		systems = applicationBean.getIApplicaitons().stream()
				.map(app -> new SecuritySystem(app.getSecurityContext()))
				.collect(Collectors.toList());
	}
	
	public List<SecuritySystem> getSecuritySystems() {
		return systems;
	}
	
	public void triggerSynchronization(long securityContextId) {
		Ivy.log().info("trigger");
		SynchronizationListener listener = new SynchronizationListener() {
			
			@Override
			public void handleUpdate(UpdateEvent updateEvent) {
				Ivy.log().info("update");
			}
			
			@Override
			public void handleLog(Level level, String message, Throwable exception) {
				Ivy.log().info("log");
			}
			
			@Override
			public void handleFinished(UpdateEvent finalEvent) {
				Ivy.log().info("finished");
			}
		};
		Optional<ISecurityContext> context = applicationBean.getIApplicaitons().stream()
				.map(app -> app.getSecurityContext())
				.filter(c -> c.getId() == securityContextId)
				.findAny();
		if (context.isPresent()) {
			context.get().triggerSynchronization(listener);
		}
	}
	
	public boolean syncRunning(long securityContextId) {
		Optional<ISecurityContext> context = applicationBean.getIApplicaitons().stream()
				.map(app -> app.getSecurityContext())
				.filter(c -> c.getId() == securityContextId)
				.findAny();
		if (context.isPresent()) {
			return context.get().isSynchronizationRunning();
		}
		return false;
	}
	
}
