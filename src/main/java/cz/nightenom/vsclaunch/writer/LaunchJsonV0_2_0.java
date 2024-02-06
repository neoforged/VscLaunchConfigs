package cz.nightenom.vsclaunch.writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonStreamParser;
import cz.nightenom.vsclaunch.AttachConfiguration;
import cz.nightenom.vsclaunch.BatchedLaunchWriter;
import cz.nightenom.vsclaunch.JavaConfiguration;
import cz.nightenom.vsclaunch.LaunchConfiguration;
import cz.nightenom.vsclaunch.attribute.ConsoleType;
import cz.nightenom.vsclaunch.attribute.InternalConsoleBehaviour;
import cz.nightenom.vsclaunch.attribute.LaunchGroupEntry;
import cz.nightenom.vsclaunch.attribute.PathWritable;
import cz.nightenom.vsclaunch.attribute.RequestType;
import cz.nightenom.vsclaunch.attribute.ShortCmdBehaviour;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class LaunchJsonV0_2_0 implements IWriter
{
    private static final String VERSION_KEY = "version";
    private static final String VERSION_VALUE = "0.2.0";
    private static final String CONFIGURATIONS_KEY = "configurations";

    private static final String CFG_TYPE_KEY = "type";
    private static final String CFG_NAME_KEY = "name";
    private static final String CFG_REQUEST_KEY = "request";
    private static final String CFG_PRESENTATION_KEY = "presentation";
    private static final String PRESENTATION_GROUP_KEY = "group";
    private static final String PRESENTATION_INDEX_KEY = "order";
    private static final String PRESENTATION_VISIBILITY_KEY = "hidden";
    private static final String CFG_PRE_TASK_KEY = "preLaunchTask";
    private static final String CFG_POST_TAKS_KEY = "postDebugTask";
    private static final String CFG_INTERNAL_CONSOLE_BEHAVIOUR_KEY = "internalConsoleOptions";

    private static final String CFG_JAVA_PROJECT_NAME_KEY = "projectName";
    private static final String CFG_JAVA_SOURCE_PATHS_KEY = "sourcePaths";
    private static final String CFG_JAVA_STEP_FILTERS_KEY = "stepFilters";

    private static final String CFG_LAUNCH_MAIN_CLASS_KEY = "mainClass";
    private static final String CFG_LAUNCH_ARGUMENTS_KEY = "args";
    private static final String CFG_LAUNCH_MODULE_PATHS_KEY = "modulePaths";
    private static final String CFG_LAUNCH_CLASS_PATHS_KEY = "classPaths";
    private static final String CFG_LAUNCH_ENCODING_KEY = "encoding";
    private static final String CFG_LAUNCH_VM_ARGUMENTS_KEY = "vmArgs";
    private static final String CFG_LAUNCH_WORKING_DIRECTORY_KEY = "cwd";
    private static final String CFG_LAUNCH_ENVIRONMENT_PROPS_KEY = "env";
    private static final String CFG_LAUNCH_ENVIRONMENT_FILE_KEY = "envFile";
    private static final String CFG_LAUNCH_STOP_ON_ENTRY_KEY = "stopOnEntry";
    private static final String CFG_LAUNCH_CONSOLE_TYPE_KEY = "console";
    private static final String CFG_LAUNCH_SHORTEN_CMD_TYPE_KEY = "shortenCommandLine";

    private static final String CFG_ATTACH_HOSTNAME_KEY = "hostName";
    private static final String CFG_ATTACH_PORT_KEY = "port";
    private static final String CFG_ATTACH_PROCESS_ID_KEY = "processId";
    private static final String CFG_ATTACH_PROCESS_ID_PICKER_VALUE = "${command:PickJavaProcess}";
    private static final String CFG_ATTACH_TIMEOUT_KEY = "timeout";

    public static final LaunchJsonV0_2_0 INSTANCE = new LaunchJsonV0_2_0();

    private final Map<Enum<?>, String> enumAttributes = new IdentityHashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();

    private LaunchJsonV0_2_0()
    {
        enumAttributes.put(ConsoleType.INTERNAL_CONSOLE, "internalConsole");
        enumAttributes.put(ConsoleType.INTEGRATED_TERMINAL, "integratedTerminal");
        enumAttributes.put(ConsoleType.EXTERNAL_TERMINAL, "externalTerminal");
        enumAttributes.put(InternalConsoleBehaviour.NEVER_OPEN, "neverOpen");
        enumAttributes.put(InternalConsoleBehaviour.OPEN_ON_FIRST_SESSION_START, "openOnFirstSessionStart");
        enumAttributes.put(InternalConsoleBehaviour.OPEN_ON_SESSION_START, "openOnSessionStart");
        enumAttributes.put(RequestType.ATTACH, "attach");
        enumAttributes.put(RequestType.LAUNCH, "launch");
        enumAttributes.put(ShortCmdBehaviour.NONE, "none");
        enumAttributes.put(ShortCmdBehaviour.JAR_MANIFEST, "jarmanifest");
        enumAttributes.put(ShortCmdBehaviour.ARGUMENT_FILE, "argfile");
        enumAttributes.put(ShortCmdBehaviour.AUTO, "auto");
    }

    @Override
    public void write(final BatchedLaunchWriter data, final Path projectRoot) throws IOException
    {
        try
        {
            write0(data, projectRoot);
        }
        catch (final JsonParseException e)
        {
            throw new IOException("Error while parsing current launch.json", e);
        }
    }

    private void write0(final BatchedLaunchWriter data, final Path projectRoot) throws IOException
    {
        final Path launchJson = resolveLaunchFilePath(projectRoot);
        final JsonObject root;
        if (data.getKeepCurrentContent() == WritingMode.MODIFY_CURRENT && Files.exists(launchJson))
        {
            try
            {
                root = new JsonStreamParser(Files.newBufferedReader(launchJson)).next().getAsJsonObject();
            }
            catch (final NoSuchElementException | IllegalStateException e)
            {
                throw new JsonParseException("Malformed launch.json - missing root object", e);
            }
        }
        else
        {
            root = new JsonObject();
        }

        checkVersion(root);

        final JsonArray configs = JsonUtils.getArrayOrSetDefault(root, CONFIGURATIONS_KEY, new JsonArray());

        checkGroupRemoval(configs, data.getGroupConfigurations());

        final Map<String, JsonObject> existingConfigs = getExistingConfigs(configs);

        data.getAllConfigurations().forEach(cfg -> {
            JsonObject cfgObject = existingConfigs.get(cfg.getName());

            if (cfgObject == null)
            {
                cfgObject = new JsonObject();
                configs.add(cfgObject);
            }

            serializeConfiguration(cfgObject, cfg, projectRoot);
        });

        Files.createDirectories(launchJson.getParent());
        try (BufferedWriter writer = Files
            .newBufferedWriter(launchJson, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
        {
            gson.toJson(root, gson.newJsonWriter(writer));
        }
    }

    public static Path resolveLaunchFilePath(final Path projectRoot)
    {
        return projectRoot.resolve(".vscode").resolve("launch.json");
    }

    private Map<String, JsonObject> getExistingConfigs(final JsonArray configs)
    {
        final Map<String, JsonObject> result = new HashMap<>();

        for (final JsonObject jsonObject : JsonUtils.getObjectsFromArray(configs))
        {
            final String name = JsonUtils.getStringOrKeep(jsonObject, CFG_NAME_KEY);

            if (name != null)
            {
                result.put(name, jsonObject);
            }
        }

        return result;
    }

    private void checkGroupRemoval(final JsonArray configs, final List<GroupConfiguration> groupConfigurations)
    {
        final Set<String> forRemoval = new HashSet<>();

        for (final GroupConfiguration groupConfiguration : groupConfigurations)
        {
            if (groupConfiguration.getWritingMode() == WritingMode.MODIFY_CURRENT)
            {
                continue;
            }

            forRemoval.add(groupConfiguration.getConfigurationGroup().getName());
        }

        final Iterator<JsonElement> it = configs.iterator();
        while (it.hasNext())
        {
            final JsonElement element = it.next();
            if (!element.isJsonObject())
            {
                continue;
            }

            final JsonObject config = element.getAsJsonObject();
            final JsonObject presentation = JsonUtils.getObjectOrKeep(config, CFG_PRESENTATION_KEY);
            final String presentationGroup = JsonUtils.getStringOrKeep(presentation, PRESENTATION_GROUP_KEY);

            if (forRemoval.contains(presentationGroup))
            {
                it.remove();
            }
        }
    }

    private void checkVersion(final JsonObject root)
    {
        final String version = JsonUtils.getStringOrSetDefault(root, VERSION_KEY, VERSION_VALUE).trim();

        if (!version.isEmpty() && !version.equals(VERSION_VALUE))
        {
            throw new JsonParseException("Launch.json version is not " + VERSION_VALUE);
        }
    }

    private void serializeConfiguration(final JsonObject cfgObject, final JavaConfiguration<?> cfg, final Path workspaceFolder)
    {
        cfgObject.addProperty(CFG_TYPE_KEY, cfg.getType());
        cfgObject.addProperty(CFG_REQUEST_KEY, enumAttributes.get(cfg.getRequestType()));
        cfgObject.addProperty(CFG_NAME_KEY, cfg.getName());

        cfgObject.add(CFG_PRESENTATION_KEY,
            serializePresentation(cfg, JsonUtils.getObjectOrSetDefault(cfgObject, CFG_PRESENTATION_KEY, new JsonObject())));
        serializeNullable(cfgObject, CFG_PRE_TASK_KEY, cfg.getPreTaskName());
        serializeNullable(cfgObject, CFG_POST_TAKS_KEY, cfg.getPostTaskName());
        serializeNullable(cfgObject, CFG_INTERNAL_CONSOLE_BEHAVIOUR_KEY, cfg.getInternalConsoleOptions());

        cfgObject.addProperty(CFG_JAVA_PROJECT_NAME_KEY, cfg.getProjectName());
        serializeNullablePaths(cfgObject, CFG_JAVA_SOURCE_PATHS_KEY, cfg.getAdditionalSourcePaths(), workspaceFolder);

        if (cfg instanceof LaunchConfiguration)
        {
            final LaunchConfiguration launchCfg = (LaunchConfiguration) cfg;

            cfgObject.addProperty(CFG_LAUNCH_MAIN_CLASS_KEY, launchCfg.getMainClass());
            serializeNullable(cfgObject, CFG_LAUNCH_ARGUMENTS_KEY, launchCfg.getArguments());
            serializeNullablePaths(cfgObject, CFG_LAUNCH_MODULE_PATHS_KEY, launchCfg.getModulePathsOverride(), workspaceFolder);
            serializeNullablePaths(cfgObject, CFG_LAUNCH_CLASS_PATHS_KEY, launchCfg.getClassPathsOverride(), workspaceFolder);
            serializeNullable(cfgObject, CFG_LAUNCH_ENCODING_KEY, launchCfg.getFileEncoding());
            serializeNullable(cfgObject, CFG_LAUNCH_VM_ARGUMENTS_KEY, launchCfg.getAdditionalJvmArgs());
            serializeNullable(cfgObject, CFG_LAUNCH_WORKING_DIRECTORY_KEY, launchCfg.getCurrentWorkingDirectory(), workspaceFolder);
            serializeNullable(cfgObject, CFG_LAUNCH_ENVIRONMENT_PROPS_KEY, launchCfg.getEnvironmentVariables());
            serializeNullable(cfgObject, CFG_LAUNCH_ENVIRONMENT_FILE_KEY, launchCfg.getEnvironmentVariablesFile(), workspaceFolder);
            serializeNullable(cfgObject, CFG_LAUNCH_STOP_ON_ENTRY_KEY, launchCfg.shouldStopAppEntry());
            serializeNullable(cfgObject, CFG_LAUNCH_CONSOLE_TYPE_KEY, launchCfg.getConsoleType());
            serializeNullable(cfgObject, CFG_LAUNCH_SHORTEN_CMD_TYPE_KEY, launchCfg.getShortenCommandLine());
        }
        else if (cfg instanceof AttachConfiguration)
        {
            final AttachConfiguration attachCfg = (AttachConfiguration) cfg;

            cfgObject.addProperty(CFG_ATTACH_HOSTNAME_KEY, attachCfg.getHostName());
            cfgObject.addProperty(CFG_ATTACH_PORT_KEY, attachCfg.getPort());
            serializeNullable(cfgObject, CFG_ATTACH_TIMEOUT_KEY, attachCfg.getTimeout());

            if (attachCfg.usesProcessPicker() != null)
            {
                if (attachCfg.usesProcessPicker())
                {
                    cfgObject.addProperty(CFG_ATTACH_PROCESS_ID_KEY, CFG_ATTACH_PROCESS_ID_PICKER_VALUE);
                }
                else
                {
                    serializeNullable(cfgObject, CFG_ATTACH_PROCESS_ID_KEY, attachCfg.getProcessId());
                }
            }
        }
    }

    private JsonObject serializePresentation(final JavaConfiguration<?> cfg, final JsonObject currentPresentation)
    {
        final LaunchGroupEntry<?> groupEntry = cfg.getLaunchGroupEntry();

        serializeNullable(currentPresentation, PRESENTATION_GROUP_KEY, groupEntry.getGroupName());
        serializeNullable(currentPresentation, PRESENTATION_INDEX_KEY, groupEntry.getIndexInGroup());
        serializeNullable(currentPresentation, PRESENTATION_VISIBILITY_KEY, groupEntry.isVisibleInGroup());

        return currentPresentation;
    }

    private void serializeNullable(final JsonObject object, final String key, final String value)
    {
        if (value != null)
        {
            object.addProperty(key, value);
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final Number value)
    {
        if (value != null)
        {
            object.addProperty(key, value);
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final Boolean value)
    {
        if (value != null)
        {
            object.addProperty(key, value);
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final Enum<?> value)
    {
        if (value != null)
        {
            object.addProperty(key, enumAttributes.get(value));
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final Charset value)
    {
        if (value != null)
        {
            object.addProperty(key, value.name());
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final PathWritable value, final Path workspaceFolder)
    {
        if (value != null)
        {
            final StringBuilder pathString = new StringBuilder();
            value.write(pathString, workspaceFolder);
            object.addProperty(key, pathString.toString());
        }
    }

    private void serializeNullablePaths(final JsonObject object,
        final String key,
        final List<? extends PathWritable> value,
        final Path workspaceFolder)
    {
        if (value != null)
        {
            final JsonArray array = new JsonArray();
            object.add(key, array);

            value.forEach(path -> {
                if (path != null)
                {
                    final StringBuilder pathString = new StringBuilder();
                    path.write(pathString, workspaceFolder);
                    array.add(pathString.toString());
                }
            });
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final List<?> value)
    {
        if (value != null)
        {
            final JsonArray array = new JsonArray();
            object.add(key, array);
            value.forEach(obj -> {
                if (obj != null)
                {
                    array.add(obj.toString());
                }
            });
        }
    }

    private void serializeNullable(final JsonObject object, final String key, final Map<?, ?> value)
    {
        if (value != null)
        {
            final JsonObject map = new JsonObject();
            object.add(key, map);
            value.forEach((k, v) -> {
                if (k != null && v != null)
                {
                    map.addProperty(k.toString(), v.toString());
                }
            });
        }
    }
}
