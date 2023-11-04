package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attribute.RequestType;

public class AttachConfiguration extends JavaConfiguration<AttachConfiguration>
{
    private String hostName;
    private Integer port;
    private Boolean useProcessPicker;
    private Integer processId;
    private Integer timeout;

    protected AttachConfiguration(final ConfigurationGroup parentGroup, final int defaultInGroupIndex)
    {
        super(parentGroup, defaultInGroupIndex);
    }

    protected AttachConfiguration()
    {}

    @Override
    public RequestType getRequestType()
    {
        return RequestType.ATTACH;
    }

    public String getHostName()
    {
        return hostName;
    }

    public AttachConfiguration withHostName(final String hostName)
    {
        this.hostName = hostName;
        return this;
    }

    public Integer getPort()
    {
        return port;
    }

    public AttachConfiguration withPort(final Integer port)
    {
        this.port = port;
        return this;
    }

    public Boolean usesProcessPicker()
    {
        return useProcessPicker;
    }

    public AttachConfiguration withProcessPicker()
    {
        this.useProcessPicker = true;
        return this;
    }

    public Integer getProcessId()
    {
        return processId;
    }

    public AttachConfiguration withProcessId(final Integer processId)
    {
        this.processId = processId;
        this.useProcessPicker = false;
        return this;
    }

    public Integer getTimeout()
    {
        return timeout;
    }

    public AttachConfiguration withTimeout(final Integer timeout)
    {
        this.timeout = timeout;
        return this;
    }

    @Override
    protected String validate()
    {
        if (hostName == null) return "Missing hostName";
        else if (port == null) return "Missing port";
        else return super.validate();
    }
}
