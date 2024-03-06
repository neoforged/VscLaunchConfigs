package net.neoforged.vsclc;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationGroup
{
    private final String name;
    private final List<JavaConfiguration<?>> children = new ArrayList<>();

    protected ConfigurationGroup(final String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public LaunchConfiguration createLaunchConfiguration()
    {
        final LaunchConfiguration cfg = new LaunchConfiguration(this, children.size());
        children.add(cfg);
        return cfg;
    }

    public AttachConfiguration createAttachConfiguration()
    {
        final AttachConfiguration cfg = new AttachConfiguration(this, children.size());
        children.add(cfg);
        return cfg;
    }

    public List<JavaConfiguration<?>> getConfigurations()
    {
        return children;
    }
}
