package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attribute.InternalConsoleBehaviour;
import cz.nightenom.vsclaunch.attribute.LaunchGroupEntry;
import cz.nightenom.vsclaunch.attribute.PathLike;
import cz.nightenom.vsclaunch.attribute.RequestType;
import java.util.List;

/**
 * Version agnostic interface. Based on: https://code.visualstudio.com/docs/editor/debugging#_launchjson-attributes,
 * https://code.visualstudio.com/docs/java/java-debugging#_configuration-options
 */
public abstract class JavaConfiguration<T extends JavaConfiguration<T>>
{
    @SuppressWarnings("unchecked")
    private final T thiz = (T) this;
    private final ConfigurationGroup parentGroup;

    private String name;
    private LaunchGroupEntry<T> launchGroupEntry;
    private String preTaskName;
    private String postTaskName;
    private InternalConsoleBehaviour internalConsoleOptions;

    private String projectName;
    private List<PathLike> additionalSourcePaths;
    // TODO: stepFilters (way too much work for now)

    protected JavaConfiguration(final ConfigurationGroup parentGroup, final int defaultInGroupIndex)
    {
        this.parentGroup = parentGroup;
        this.launchGroupEntry =
            new LaunchGroupEntry<>(thiz).withGroupName(parentGroup.getName()).withIndexInGroup(defaultInGroupIndex);
    }

    protected JavaConfiguration()
    {
        this.parentGroup = null;
    }

    /**
     * @return debugger type
     */
    public String getType()
    {
        return "java";
    }

    public abstract RequestType getRequestType();

    public String getName()
    {
        return name;
    }

    public T withName(final String name)
    {
        this.name = name;
        return thiz;
    }

    public LaunchGroupEntry<T> getLaunchGroupEntry()
    {
        return launchGroupEntry;
    }

    public T withLaunchGroupEntry(final LaunchGroupEntry<T> launchGroupEntry)
    {
        this.launchGroupEntry = launchGroupEntry;
        return thiz;
    }

    public String getPreTaskName()
    {
        return preTaskName;
    }

    public T withPreTaskName(final String preTaskName)
    {
        this.preTaskName = preTaskName;
        return thiz;
    }

    public String getPostTaskName()
    {
        return postTaskName;
    }

    public T withPostTaskName(final String postTaskName)
    {
        this.postTaskName = postTaskName;
        return thiz;
    }

    public InternalConsoleBehaviour getInternalConsoleOptions()
    {
        return internalConsoleOptions;
    }

    public T withInternalConsoleOptions(final InternalConsoleBehaviour internalConsoleOptions)
    {
        this.internalConsoleOptions = internalConsoleOptions;
        return thiz;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public T withProjectName(final String projectName)
    {
        this.projectName = projectName;
        return thiz;
    }

    public List<PathLike> getAdditionalSourcePaths()
    {
        return additionalSourcePaths;
    }

    public T withAdditionalSourcePaths(final List<PathLike> additionalSourcePaths)
    {
        this.additionalSourcePaths = additionalSourcePaths;
        return thiz;
    }

    public ConfigurationGroup backToParentGroup()
    {
        return parentGroup;
    }

    protected String validate()
    {
        if (name == null) return "Missing name";
        else if (projectName == null) return "Missing projectName";
        else return null;
    }
}
