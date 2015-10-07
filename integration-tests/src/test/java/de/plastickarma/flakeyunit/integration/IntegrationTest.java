package de.plastickarma.flakeyunit.integration;

import de.plastickarma.flakeyunit.CountingFlakeyTestcaseListener;
import de.plastickarma.flakeyunit.FlakeyTestIndicatorBuilder;
import org.junit.*;
import org.junit.rules.TestRule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Integration test for flakey test case detection actual junit lib. We are using @BeforeClass and @AfterClass
 * to setup and check the expected results. It is checked that setup and tear-down methods are called correctly
 * and that the flakey test case is detected. Note, that this test class must not have more than one test method.
 * To add another integration test, add a new class.
 */
public class IntegrationTest {

    private static CountingFlakeyTestcaseListener counter = null;
    private static final int RERUNS = 5;

    private static boolean waitingForBefore = true;
    private static boolean waitingForAfter = false;
    private static int beforeCount = 0;
    private static int afterCount = 0;
    private static int testrunCount = 0;


    /**
     * Initializes the integration test.
     */
    @BeforeClass
    public static void initializeTestcase() {
        beforeCount = 0;
        afterCount = 0;
        testrunCount = 0;
        waitingForAfter = false;
        waitingForBefore = true;
        counter = new CountingFlakeyTestcaseListener();
    }

    /**
     * Checks the output of the integration test.
     */
    @AfterClass
    public static void checkTestRuns() {
        assertThat(waitingForAfter, is(false));
        assertThat(waitingForBefore, is(true));
        assertThat(beforeCount, is(RERUNS + 1));
        assertThat(afterCount, is(RERUNS + 1));
        assertThat(testrunCount, is(RERUNS + 1));

        assertThat(counter.getFlakeyTests().size(), is(1));
        assertThat(counter.getFlakeyTests().get(0), is("flakeyTest"));
    }

    /**
     * Creates the flakey test case indicator.
     */
    @Rule
    public TestRule flakeyTestcaseIndicator() {
        return FlakeyTestIndicatorBuilder.indicator()
                .listener(counter)
                .rethrowOriginal(false)
                .retries(RERUNS)
                .build();
    }

    /**
     * Checks that the @After method is called correctly.
     */
    @After
    public void afterTest() {
        afterCount++;
        assertThat(waitingForBefore, is(false));
        assertThat(waitingForAfter, is(true));
        waitingForBefore = true;
        waitingForAfter = false;
    }

    /**
     * Checks that the @Before method is called correctly.
     */
    @Before
    public void beforeTest() {
        beforeCount++;
        assertThat(waitingForBefore, is(true));
        assertThat(waitingForAfter, is(false));
        waitingForBefore = false;
        waitingForAfter = true;
    }

    private static int suspiciousGlobalState = 1;

    /**
     * The flakey test case.
     */
    @Test public void flakeyTest() {
        testrunCount++;
        assertThat(suspiciousGlobalState++ % 2, is(0));
    }
}
