package net.neoforged.vsclc.writer;

import net.neoforged.vsclc.BatchedLaunchWriter;
import net.neoforged.vsclc.ConfigurationGroup;

import java.io.IOException;
import java.nio.file.Path;

public interface IWriter
{
    void write(BatchedLaunchWriter data, Path projectRoot) throws IOException;

    public static class GroupConfiguration
    {
        private final ConfigurationGroup configurationGroup;
        private final WritingMode writingMode;

        public GroupConfiguration(final ConfigurationGroup configurationGroup, final WritingMode writingMode)
        {
            this.configurationGroup = configurationGroup;
            this.writingMode = writingMode;
        }

        public ConfigurationGroup getConfigurationGroup()
        {
            return configurationGroup;
        }

        public WritingMode getWritingMode()
        {
            return writingMode;
        }
    }
}
