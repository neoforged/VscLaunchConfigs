package net.neoforged.vsclc.attribute;

import net.neoforged.vsclc.JavaConfiguration;

public class LaunchGroupEntry<T extends JavaConfiguration<T>>
{
    private final T parent;

    private String groupName = null;
    private Boolean visibilityInGroup = null;
    private Integer indexInGroup = null;

    public LaunchGroupEntry(final T parent)
    {
        this.parent = parent;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public LaunchGroupEntry<T> withGroupName(final String groupName)
    {
        this.groupName = groupName;
        return this;
    }

    public Boolean isVisibleInGroup()
    {
        return visibilityInGroup;
    }

    public LaunchGroupEntry<T> withVisibilityInGroup(final Boolean isVisible)
    {
        this.visibilityInGroup = isVisible;
        return this;
    }

    public Integer getIndexInGroup()
    {
        return indexInGroup;
    }

    public LaunchGroupEntry<T> withIndexInGroup(final Integer index)
    {
        this.indexInGroup = index;
        return this;
    }

    public T backToConfiguration()
    {
        return parent;
    }
}
