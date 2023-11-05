package cz.nightenom.vsclaunch.attribute;

import java.io.File;
import java.nio.file.Path;

public interface LocatorPathLike extends PathWritable
{
    LocatorPathLike AUTO = new StringPath("$Auto");
    LocatorPathLike RUNTIME = new StringPath("$Runtime");
    LocatorPathLike TEST = new StringPath("$Test");

    public static LocatorPathLike ofAuto()
    {
        return AUTO;
    }

    public static LocatorPathLike ofRuntime()
    {
        return RUNTIME;
    }

    public static LocatorPathLike ofTest()
    {
        return TEST;
    }

    public static LocatorPathLike ofNioExclude(final Path exclusionPath)
    {
        return new NioPathWithPrefix("!", exclusionPath);
    }

    public static LocatorPathLike ofWorkSpaceFolderExclude(final Path relativeExclusionPath)
    {
        return new NioPathWithPrefix("!" + PathWritable.WORKSPACE_FOLDER + File.separator, relativeExclusionPath);
    }

    public static LocatorPathLike ofNio(final Path path)
    {
        return new NioPath(path);
    }

    public static LocatorPathLike ofWorkSpaceFolder(final Path relativePath)
    {
        return new NioPathWithPrefix(PathWritable.WORKSPACE_FOLDER + File.separator, relativePath);
    }

    static class StringPath implements LocatorPathLike
    {
        private final String value;

        private StringPath(final String value)
        {
            this.value = value;
        }

        @Override
        public void write(final StringBuilder sink, final Path workspaceFolder)
        {
            sink.append(value);
        }
    }

    static class NioPath implements LocatorPathLike
    {
        private final Path path;

        private NioPath(final Path path)
        {
            this.path = path;
        }

        @Override
        public void write(final StringBuilder sink, final Path workspaceFolder)
        {
            sink.append(PathWritable.pathToRelativeString(path, workspaceFolder));
        }
    }

    static class NioPathWithPrefix extends NioPath
    {
        private final String prefix;

        private NioPathWithPrefix(final String prefix, final Path path)
        {
            super(path);
            this.prefix = prefix;
        }

        @Override
        public void write(final StringBuilder sink, final Path workspaceFolder)
        {
            sink.append(prefix);
            super.write(sink, workspaceFolder);
        }
    }
}
