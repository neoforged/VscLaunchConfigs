package cz.nightenom.vsclaunch.attribute;

import java.io.File;
import java.nio.file.Path;

public interface PathLike extends PathWritable
{
    public static PathLike ofNio(final Path path)
    {
        return new NioPath(path);
    }

    public static PathLike ofWorkSpaceFolder(final Path relativePath)
    {
        return new NioPathWithPrefix(PathWritable.WORKSPACE_FOLDER, relativePath);
    }

    static class NioPath implements PathLike
    {
        private final Path path;

        private NioPath(final Path path)
        {
            this.path = path;
        }

        @Override
        public void write(final StringBuilder sink)
        {
            String value = path.toString();
            if (File.separatorChar == '\\')
            {
                value = value.replace('\\', '/');
            }
            sink.append(value);
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
        public void write(final StringBuilder sink)
        {
            sink.append(prefix);
            sink.append('/');
            super.write(sink);
        }
    }
}
