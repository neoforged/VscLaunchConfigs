package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attribute.ConsoleType;
import cz.nightenom.vsclaunch.attribute.LocatorPathLike;
import cz.nightenom.vsclaunch.attribute.PathLike;
import cz.nightenom.vsclaunch.attribute.ShortCmdBehaviour;
import cz.nightenom.vsclaunch.writer.WritingMode;
import org.junit.jupiter.api.Test;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BatchedLaunchWriterTest
{
    @Test
    void testWriteToLatestJson()
    {
        final Path targetDir = Paths.get(".").resolve("run");
        assertDoesNotThrow(() -> Files.createDirectories(targetDir));
        System.out.println("Running in: " + targetDir.toAbsolutePath().normalize().toString());

        final List<LocatorPathLike> locatorTestPaths = Arrays.asList(LocatorPathLike.ofAuto(),
            LocatorPathLike.ofNio(Paths.get(".", "test")),
            LocatorPathLike.ofNioExclude(Paths.get(".", "exclude")),
            LocatorPathLike.ofRuntime(),
            LocatorPathLike.ofTest(),
            LocatorPathLike.ofWorkSpaceFolder(Paths.get("workspaceF")),
            LocatorPathLike.ofWorkSpaceFolderExclude(Paths.get("workspaceFexclude")));

        final Map<String, String> testEnv = new HashMap<>();
        testEnv.put("whoami", "myself");
        testEnv.put("whoru", "yourself");

        final BatchedLaunchWriter writer = new BatchedLaunchWriter(WritingMode.MODIFY_CURRENT);
        writer.createGroup("test", WritingMode.REMOVE_EXISTING)
            .createLaunchConfiguration()
            .withName("launchTest")
            .withProjectName("coolProject")
            .withMainClass("coolProject.Main")
            .withArguments(Arrays.asList("--help", "--version"))
            .withAdditionalJvmArgs(Arrays.asList("-Xmx4G"))
            .withModulePathsOverride(locatorTestPaths)
            .withClassPathsOverride(Collections.emptyList())
            .withFileEncoding(Charset.forName("UTF-16"))
            .withCurrentWorkingDirectory(PathLike.ofWorkSpaceFolder(Paths.get("run")))
            .withEnvironmentVariables(testEnv)
            .withEnvironmentVariablesFile(PathLike.ofNio(Paths.get(".", "envFile")))
            .withShouldStopAppEntry(true)
            .withConsoleType(ConsoleType.INTERNAL_CONSOLE)
            .withShortenCommandLine(ShortCmdBehaviour.ARGUMENT_FILE)
            .backToParentGroup()
            //
            .createAttachConfiguration()
            .withProjectName("coolProject")
            .withName("attachTest")
            .withHostName("localhost")
            .withPort(55555)
            .withTimeout(100)
            .withProcessId(10)
            .backToParentGroup()
            //
            .createAttachConfiguration()
            .withProjectName("coolProject")
            .withName("attachTestProjectPicker")
            .withHostName("localhost")
            .withPort(55555)
            .withTimeout(100)
            .withProcessPicker();

        assertDoesNotThrow(() -> writer.writeToLatestJson(targetDir));
    }
}
