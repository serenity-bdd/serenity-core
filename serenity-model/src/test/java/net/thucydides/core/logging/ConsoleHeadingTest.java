package net.thucydides.core.logging;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsoleHeadingTest {

  @Test
  public void createBorderAroundEventTitle() {
    assertThat(ConsoleHeading.titleWithBorder(ConsoleEvent.TEST_PENDING)).contains("TEST PENDING");
  }
}
