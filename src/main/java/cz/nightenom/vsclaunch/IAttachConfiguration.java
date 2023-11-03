package cz.nightenom.vsclaunch;

public interface IAttachConfiguration extends IJavaConfiguration
{
    String getHostname();

    int getPort();

    // TODO: there is also weird picker option
    int getProcessId();

    int getTimeout();
}
