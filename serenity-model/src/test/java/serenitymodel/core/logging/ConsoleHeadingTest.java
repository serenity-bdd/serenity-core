package serenitymodel.core.logging;

import org.junit.Test;
import serenitymodel.net.thucydides.core.logging.ConsoleEvent;
import serenitymodel.net.thucydides.core.logging.ConsoleHeading;

import static org.junit.Assert.*;

public class ConsoleHeadingTest {

  @Test
  public void createBorderAroundEventTitle() {

    assertEquals(
        "----------------\n" +
            "- TEST PENDING -\n" +
            "----------------\n",
        ConsoleHeading.titleWithBorder(ConsoleEvent.TEST_PENDING)
    );
  }
}