
package javajo.sample.tests.sheepdog;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import javajo.sample.TestTime;

import static org.assertj.core.api.Assertions.assertThat;

public class SheepdogTest {

    @Rule
    public final TestTime testTime = new TestTime();

    @After
    public void teardown() {
        System.out.println(testTime.getStamp());
    }

    @Test
    public void testName() throws InterruptedException {
        Thread.sleep(200l);
        assertThat(testTime.getTestName())
                .isEqualTo("testName");
    }

    @Test
    public void className() throws InterruptedException {
        Thread.sleep(200l);
        assertThat(testTime.getClassName())
                .isEqualTo("javajo.sample.tests.sheepdog.SheepdogTest");
    }

    @Test
    public void stamp() throws InterruptedException {
        Thread.sleep(200l);
        assertThat(testTime.getStamp())
                .contains("javajo.sample.tests.sheepdog.SheepdogTest#stamp");
    }
}
