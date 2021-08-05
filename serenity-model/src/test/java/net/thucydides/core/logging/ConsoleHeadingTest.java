package net.thucydides.core.logging;

import org.junit.Test;

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