package cz.nightenom.vsclaunch;

import cz.nightenom.vsclaunch.attribute.ConsoleType;
import cz.nightenom.vsclaunch.attribute.LocatorPathLike;
import cz.nightenom.vsclaunch.attribute.PathLike;
import cz.nightenom.vsclaunch.attribute.ShortCmdBehaviour;
import cz.nightenom.vsclaunch.writer.LaunchJsonV0_2_0;
import cz.nightenom.vsclaunch.writer.WritingMode;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
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
import static org.junit.jupiter.api.Assertions.assertNull;

public class BatchedLaunchWriterTest
{
    @Test
    void testWriteToLatestJson()
    {
        final Path targetDir = Paths.get(".").resolve("run");
        assertDoesNotThrow(() -> Files.createDirectories(targetDir));
        System.out.println("Running in: " + targetDir.toAbsolutePath().normalize().toString());

        final List<LocatorPathLike> locatorTestPaths = Arrays.asList(LocatorPathLike.ofAuto(),
            LocatorPathLike.ofNio(Paths.get(".", "test").toAbsolutePath()),
            LocatorPathLike.ofNioExclude(Paths.get(".", "exclude")),
            LocatorPathLike.ofRuntime(),
            LocatorPathLike.ofTest(),
            LocatorPathLike.ofWorkSpaceFolder(Paths.get("workspaceFolder")),
            LocatorPathLike.ofWorkSpaceFolderExclude(Paths.get("workspaceFolderExclude")));

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
            .withEnvironmentVariablesFile(PathLike.ofNio(Paths.get(".", "envFile").toAbsolutePath()))
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

        assertDoesNotThrow(() -> writer.write(LaunchJsonV0_2_0.INSTANCE, targetDir.toAbsolutePath()));

        final String mismatch = assertDoesNotThrow(
            () -> areFileContentsEqual(LaunchJsonV0_2_0.resolveLaunchFilePath(Paths.get(".", "src", "test", "resources", "v0_2_0")),
                LaunchJsonV0_2_0.resolveLaunchFilePath(targetDir)));
        assertNull(mismatch, mismatch);
    }

    /**
     * @return null if same or description of mismatch
     */
    private static String areFileContentsEqual(final Path expected, final Path testResult) throws IOException
    {
        try (BufferedReader expectedReader = Files.newBufferedReader(expected);
            BufferedReader testReader = Files.newBufferedReader(testResult))
        {
            final String expectedName = expected.toAbsolutePath().normalize().toString();
            final String testName = testResult.toAbsolutePath().normalize().toString();

            long lineNumber = 1;
            String expectedLine = "", testLine = "";
            while ((expectedLine = expectedReader.readLine()) != null)
            {
                testLine = testReader.readLine();
                if (testLine == null)
                {
                    return "Generated file " + testName + " is shorter than expected test output: " + expectedName;
                }
                if (!expectedLine.equals(testLine))
                {
                    return "Generated file " + testName +
                        " has mismatch on line " +
                        lineNumber +
                        " compared to expected test output: " +
                        expectedName;
                }
                lineNumber++;
            }
            if (testReader.readLine() == null)
            {
                // there are same
                return null;
            }
            else
            {
                return "Generated file " + testName + " is longer than expected test output: " + expectedName;
            }
        }
    }
}
