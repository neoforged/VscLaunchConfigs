package net.neoforged.vsclc;

import net.neoforged.vsclc.attribute.ConsoleType;
import net.neoforged.vsclc.attribute.LocatorPathLike;
import net.neoforged.vsclc.attribute.PathLike;
import net.neoforged.vsclc.attribute.RequestType;
import net.neoforged.vsclc.attribute.ShortCmdBehaviour;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class LaunchConfiguration extends JavaConfiguration<LaunchConfiguration>
{
    private String mainClass;
    private List<String> arguments;
    private List<String> additionalJvmArgs;
    private List<LocatorPathLike> modulePathsOverride;
    private List<LocatorPathLike> classPathsOverride;
    private Charset fileEncoding;
    private PathLike currentWorkingDirectory;
    private Map<String, String> environmentVariables;
    private PathLike environmentVariablesFile;
    private Boolean shouldStopAppEntry;
    private ConsoleType consoleType;
    private ShortCmdBehaviour shortenCommandLine;

    protected LaunchConfiguration(final ConfigurationGroup parentGroup, final int defaultInGroupIndex)
    {
        super(parentGroup, defaultInGroupIndex);
    }

    protected LaunchConfiguration()
    {}

    @Override
    public RequestType getRequestType()
    {
        return RequestType.LAUNCH;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public LaunchConfiguration withMainClass(final String mainClass)
    {
        this.mainClass = mainClass;
        return this;
    }

    public List<String> getArguments()
    {
        return arguments;
    }

    public LaunchConfiguration withArguments(final List<String> arguments)
    {
        this.arguments = arguments;
        return this;
    }

    public List<String> getAdditionalJvmArgs()
    {
        return additionalJvmArgs;
    }

    public LaunchConfiguration withAdditionalJvmArgs(final List<String> additionalJvmArgs)
    {
        this.additionalJvmArgs = additionalJvmArgs;
        return this;
    }

    public List<LocatorPathLike> getModulePathsOverride()
    {
        return modulePathsOverride;
    }

    public LaunchConfiguration withModulePathsOverride(final List<LocatorPathLike> modulePathsOverride)
    {
        this.modulePathsOverride = modulePathsOverride;
        return this;
    }

    public List<LocatorPathLike> getClassPathsOverride()
    {
        return classPathsOverride;
    }

    public LaunchConfiguration withClassPathsOverride(final List<LocatorPathLike> classPathsOverride)
    {
        this.classPathsOverride = classPathsOverride;
        return this;
    }

    public Charset getFileEncoding()
    {
        return fileEncoding;
    }

    public LaunchConfiguration withFileEncoding(final Charset fileEncoding)
    {
        this.fileEncoding = fileEncoding;
        return this;
    }

    public PathLike getCurrentWorkingDirectory()
    {
        return currentWorkingDirectory;
    }

    public LaunchConfiguration withCurrentWorkingDirectory(final PathLike currentWorkingDirectory)
    {
        this.currentWorkingDirectory = currentWorkingDirectory;
        return this;
    }

    public Map<String, String> getEnvironmentVariables()
    {
        return environmentVariables;
    }

    public LaunchConfiguration withEnvironmentVariables(final Map<String, String> environmentVariables)
    {
        this.environmentVariables = environmentVariables;
        return this;
    }

    public PathLike getEnvironmentVariablesFile()
    {
        return environmentVariablesFile;
    }

    public LaunchConfiguration withEnvironmentVariablesFile(final PathLike environmentVariablesFile)
    {
        this.environmentVariablesFile = environmentVariablesFile;
        return this;
    }

    public Boolean shouldStopAppEntry()
    {
        return shouldStopAppEntry;
    }

    public LaunchConfiguration withShouldStopAppEntry(final Boolean shouldStopAppEntry)
    {
        this.shouldStopAppEntry = shouldStopAppEntry;
        return this;
    }

    public ConsoleType getConsoleType()
    {
        return consoleType;
    }

    public LaunchConfiguration withConsoleType(final ConsoleType consoleType)
    {
        this.consoleType = consoleType;
        return this;
    }

    public ShortCmdBehaviour getShortenCommandLine()
    {
        return shortenCommandLine;
    }

    public LaunchConfiguration withShortenCommandLine(final ShortCmdBehaviour shortenCommandLine)
    {
        this.shortenCommandLine = shortenCommandLine;
        return this;
    }

    @Override
    protected String validate()
    {
        if (mainClass == null) return "Missing mainClass";
        else return super.validate();
    }
}
