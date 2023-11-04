package cz.nightenom.vsclaunch.attribute;

public interface PathWritable
{
    String WORKSPACE_FOLDER = "${workspaceFolder}";

    void write(StringBuilder sink);
}
