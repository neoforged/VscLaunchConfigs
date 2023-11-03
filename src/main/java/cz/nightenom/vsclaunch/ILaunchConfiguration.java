package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attributes.ConsoleType;
import cz.nightenom.vsclaunch.attributes.LocatorPathLike;
import cz.nightenom.vsclaunch.attributes.PathLike;
import cz.nightenom.vsclaunch.attributes.ShortCmdBehaviour;
import java.util.List;
import java.util.Map;

public interface ILaunchConfiguration extends IJavaConfiguration
{
    String getAppMainClass();

    List<String> getAppArguments();

    List<String> getAdditionalJvmArgs();

    List<LocatorPathLike> getModulePathsOverride();

    List<LocatorPathLike> getClassPathsOverride();

    String getFileEncoding();

    PathLike getCurrentWorkingDirectory();

    Map<String, String> getEnvironmentVariables();

    PathLike getEnvironmentVariablesFile();

    boolean shouldStopAppEntry();

    ConsoleType getConsoleType();

    ShortCmdBehaviour getShortenCommandLine();
}
