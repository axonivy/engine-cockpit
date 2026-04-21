package ch.ivyteam.enginecockpit.services;

import java.util.List;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;

public abstract class DetailView extends HelpServices implements IConnectionTestResult, PropertyEditor {

    @Override
    public abstract void removeProperty(String name);

    @Override
    public abstract List<Property> getProperties();

    @Override
    public abstract Property getProperty();

    @Override
    public abstract void saveProperty(boolean isNewProperty);

    @Override
    public abstract void setProperty(String key);

    @Override
    public abstract ConnectionTestResult getResult();

    @Override
    public abstract String getYaml();
    
}
