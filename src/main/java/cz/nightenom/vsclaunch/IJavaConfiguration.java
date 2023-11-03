package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attributes.InternalConsoleBehaviour;
import cz.nightenom.vsclaunch.attributes.LaunchGroupEntry;
import cz.nightenom.vsclaunch.attributes.PathLike;
import cz.nightenom.vsclaunch.attributes.RequestType;
import java.util.List;

/**
 * Version agnostic interface. Based on: https://code.visualstudio.com/docs/editor/debugging#_launchjson-attributes,
 * https://code.visualstudio.com/docs/java/java-debugging#_configuration-options
 */
public interface IJavaConfiguration
{
    /**
     * @return debugger type
     */
    default String getType()
    {
        return "java";
    }

    /**
     * @return type of debugger connection to app
     */
    RequestType getRequestType();

    String getName();

    LaunchGroupEntry getLaunchGroupEntry();

    String getPreTaskName();

    String getPostTaskName();

    InternalConsoleBehaviour getInternalConsoleOptions();

    String getProjectName();

    List<PathLike> getAdditionalSourcePaths();

    // TODO: stepFilters (way too much work for now)
}
