package cz.nightenom.vsclaunch;

public interface IDebugGroup
{
    String getName();

    IJavaConfiguration createLaunchConfiguration(String name);
}
