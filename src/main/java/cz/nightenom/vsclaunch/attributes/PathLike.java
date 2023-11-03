package cz.nightenom.vsclaunch.attributes;

import org.jetbrains.annotations.Nullable;
import java.nio.file.Path;

public interface PathLike
{
    PathLike ofNio(Path path);

    PathLike ofWorkSpaceFolder(@Nullable Path relativePath);
}
