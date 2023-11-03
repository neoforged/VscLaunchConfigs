package cz.nightenom.vsclaunch.attributes;

import cz.nightenom.vsclaunch.IJavaConfiguration;
import org.jetbrains.annotations.Nullable;

public class LaunchGroupEntry
{
    private final IJavaConfiguration parent;

    private String groupName = null;
    private Boolean visibilityInGroup = null;
    private Integer indexInGroup = null;

    public LaunchGroupEntry(final IJavaConfiguration parent)
    {
        this.parent = parent;
    }

    @Nullable
    public String getGroupName()
    {
        return groupName;
    }

    public LaunchGroupEntry withGroupName(final String groupName)
    {
        this.groupName = groupName;
        return this;
    }

    public boolean hasSetVisibilityInGroup()
    {
        return visibilityInGroup != null;
    }

    public boolean isVisibleInGroup()
    {
        return visibilityInGroup;
    }

    public LaunchGroupEntry withVisibilityInGroup(final boolean isVisible)
    {
        this.visibilityInGroup = isVisible;
        return this;
    }

    public boolean hasSetIndexInGroup()
    {
        return indexInGroup != null;
    }

    public int getIndexInGroup()
    {
        return indexInGroup;
    }

    public LaunchGroupEntry withIndexInGroup(final int index)
    {
        this.indexInGroup = index;
        return this;
    }

    public IJavaConfiguration backToParent()
    {
        return parent;
    }
}
