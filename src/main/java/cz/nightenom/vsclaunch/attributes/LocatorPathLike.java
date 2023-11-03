package cz.nightenom.vsclaunch.attributes;

import java.nio.file.Path;

public interface LocatorPathLike extends PathLike
{
    PathLike ofAuto();

    PathLike ofRuntime();

    PathLike ofTest();

    PathLike ofNioExclude(Path exclusionPath);

    PathLike ofNioInclude(Path exclusionPath);
}
