package net.neoforged.vsclc;

import net.neoforged.vsclc.writer.IWriter;
import net.neoforged.vsclc.writer.IWriter.GroupConfiguration;
import net.neoforged.vsclc.writer.LaunchJsonV0_2_0;
import net.neoforged.vsclc.writer.WritingMode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class BatchedLaunchWriter
{
    private final List<JavaConfiguration<?>> configurations = new ArrayList<>();
    private final List<GroupConfiguration> groupConfigurations = new ArrayList<>();

    private final WritingMode keepCurrentContent;

    public BatchedLaunchWriter(final WritingMode keepCurrentContent)
    {
        this.keepCurrentContent = keepCurrentContent;
    }

    public LaunchConfiguration createLaunchConfiguration()
    {
        final LaunchConfiguration cfg = new LaunchConfiguration();
        configurations.add(cfg);
        return cfg;
    }

    public AttachConfiguration createAttachConfiguration()
    {
        final AttachConfiguration cfg = new AttachConfiguration();
        configurations.add(cfg);
        return cfg;
    }

    public ConfigurationGroup createGroup(final String name, final WritingMode groupWritingMode)
    {
        for (final GroupConfiguration group : groupConfigurations)
        {
            if (group.getConfigurationGroup().getName().equals(name))
            {
                if (group.getWritingMode() != groupWritingMode)
                {
                    throw new IllegalArgumentException(String.format(
                        "Group with name \"%s\" is already registered with different writing mode - existing: %s, requested: %s",
                        name,
                        group.getWritingMode(),
                        groupWritingMode));
                }

                return group.getConfigurationGroup();
            }
        }

        final ConfigurationGroup group = new ConfigurationGroup(name);
        groupConfigurations.add(new GroupConfiguration(group, groupWritingMode));
        return group;
    }

    public void writeToLatestJson(final Path projectRoot) throws IOException
    {
        write(LaunchJsonV0_2_0.INSTANCE, projectRoot);
    }

    public void write(final IWriter writer, final Path projectRoot) throws IOException
    {
        final Set<String> nameDeduplicator = new HashSet<>();

        getAllConfigurations().forEach(cfg -> {
            final String validationError = cfg.validate();
            if (validationError != null)
            {
                throw new IllegalStateException(
                    String.format("Invalid configuration with name \"%s\" - %s", cfg.getName(), validationError));
            }

            if (!nameDeduplicator.add(cfg.getName()))
            {
                throw new IllegalStateException("Duplicate configuration name: " + cfg.getName());
            }
        });

        writer.write(this, projectRoot);
    }

    // internal
    public Stream<JavaConfiguration<?>> getAllConfigurations()
    {
        return Stream.concat(configurations.stream(),
            groupConfigurations.stream()
                .map(GroupConfiguration::getConfigurationGroup)
                .map(ConfigurationGroup::getConfigurations)
                .flatMap(List::stream));
    }

    // internal
    public List<JavaConfiguration<?>> getConfigurations()
    {
        return configurations;
    }

    // internal
    public List<GroupConfiguration> getGroupConfigurations()
    {
        return groupConfigurations;
    }

    // internal
    public WritingMode getKeepCurrentContent()
    {
        return keepCurrentContent;
    }
}
