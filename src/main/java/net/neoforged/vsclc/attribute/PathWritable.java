package net.neoforged.vsclc.attribute;

import java.io.File;
import java.nio.file.Path;

public interface PathWritable
{
    String WORKSPACE_FOLDER = "${workspaceFolder}";

    void write(StringBuilder sink, Path workspaceFolder);

    static String pathToRelativeString(final Path path, final Path workspaceFolder)
    {
        if (path.isAbsolute() && workspaceFolder.isAbsolute())
        {
            try
            {
                return WORKSPACE_FOLDER + File.separator + workspaceFolder.relativize(path).toString();
            }
            catch (final IllegalArgumentException e)
            {}
        }
        return path.toString();
    }
}
