package com.immomo.momosec.gradle.plugins;

import com.immomo.momosec.gradle.plugins.exceptions.FoundVulnerableException;
import com.immomo.momosec.gradle.plugins.exceptions.NetworkErrorException;
import org.gradle.api.logging.Logger;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class TestRenderer {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private final Logger log = mock(Logger.class);
    private final Logger spyLog = Mockito.spy(log);
    private final MosecLogHelper logHelper = new MosecLogHelper();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private final String no_vulnerable_response =
        "{" +
        "  \"ok\": true," +
        "  \"dependencyCount\": 3" +
        "}";

    private final String vulnerable_response =
        "{" +
        "  \"ok\": false," +
        "  \"dependencyCount\": 3," +
        "  \"vulnerabilities\": [{" +
        "    \"severity\": \"High\"," +
        "    \"title\": \"Fake Vulnerable\"," +
        "    \"cve\": \"CVE-0001-0001\"," +
        "    \"packageName\": \"com.study.foo:bar\"," +
        "    \"version\": \"1.0.0\"," +
        "    \"target_version\": [\"1.1\"]" +
        "  }]" +
        "}";

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Before
    public void mockLog() {
        doAnswer(invocation -> {
            System.out.println((String)invocation.getArgument(0));
            return null;
        }).when(spyLog).info(anyString());

        doAnswer(invocation -> {
            System.out.println((String)invocation.getArgument(0));
            return null;
        }).when(spyLog).debug(anyString());

        doAnswer(invocation -> {
            System.out.println((String)invocation.getArgument(0));
            return null;
        }).when(spyLog).warn(anyString());

        doAnswer(invocation -> {
            System.out.println((String)invocation.getArgument(0));
            return null;
        }).when(spyLog).error(anyString());
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void renderResponseTest_ErrorJson() throws Exception {
        exceptionRule.expect(NetworkErrorException.class);
        exceptionRule.expectMessage(Constants.ERROR_ON_API);

        Renderer renderer = new Renderer(log, true);
        renderer.renderResponse(new ByteArrayInputStream("_".getBytes()));
    }

    @Test
    public void renderResponseTest_NotFoundVuln() throws Exception {
        Renderer renderer = new Renderer(spyLog, true);
        renderer.renderResponse(new ByteArrayInputStream(no_vulnerable_response.getBytes()));

        String expect = logHelper.strongInfo("✓ Tested 3 dependencies, no vulnerable found.") + "\n";
        Assert.assertEquals(expect, outContent.toString());
    }

    @Test
    public void renderResponseTest_FoundVulnWithFailOnVuln() throws Exception {
        exceptionRule.expect(FoundVulnerableException.class);
        exceptionRule.expectMessage(Constants.ERROR_ON_VULNERABLE);

        Renderer renderer = new Renderer(log, true);
        renderer.renderResponse(new ByteArrayInputStream(vulnerable_response.getBytes()));
    }

    @Test
    public void renderResponseTest_FoundVulnWithoutFailOnVuln() throws Exception {
        Renderer renderer = new Renderer(spyLog, false);
        renderer.renderResponse(new ByteArrayInputStream(vulnerable_response.getBytes()));

        String expect =
                logHelper.strongError("✗ High severity (Fake Vulnerable - CVE-0001-0001) found on com.study.foo:bar@1.0.0") + "\n" +
                logHelper.strongInfo("! Fix version [\"1.1\"]") + "\n" +
                "\n" +
                logHelper.strongWarning("Tested 3 dependencies, found 1 vulnerable pathes.") + "\n";
        Assert.assertEquals(expect, outContent.toString());
    }

}
