package net.serenitybdd.playwright;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PlaywrightPageRegistryTest {

    @AfterEach
    void cleanup() {
        PlaywrightPageRegistry.clear();
    }

    @Test
    void shouldRegisterPage() {
        Page mockPage = mock(Page.class);

        PlaywrightPageRegistry.registerPage(mockPage);

        assertThat(PlaywrightPageRegistry.getRegisteredPages()).contains(mockPage);
    }

    @Test
    void shouldUnregisterPage() {
        Page mockPage = mock(Page.class);
        PlaywrightPageRegistry.registerPage(mockPage);

        PlaywrightPageRegistry.unregisterPage(mockPage);

        assertThat(PlaywrightPageRegistry.getRegisteredPages()).isEmpty();
    }

    @Test
    void shouldClearAllPages() {
        Page page1 = mock(Page.class);
        Page page2 = mock(Page.class);
        PlaywrightPageRegistry.registerPage(page1);
        PlaywrightPageRegistry.registerPage(page2);

        PlaywrightPageRegistry.clear();

        assertThat(PlaywrightPageRegistry.getRegisteredPages()).isEmpty();
    }

    @Test
    void shouldNotRegisterDuplicates() {
        Page mockPage = mock(Page.class);

        PlaywrightPageRegistry.registerPage(mockPage);
        PlaywrightPageRegistry.registerPage(mockPage);

        assertThat(PlaywrightPageRegistry.getRegisteredPages()).hasSize(1);
    }

    @Test
    void shouldNotRegisterNullPage() {
        PlaywrightPageRegistry.registerPage(null);

        assertThat(PlaywrightPageRegistry.getRegisteredPages()).isEmpty();
    }

    @Test
    void shouldReportHasRegisteredPages() {
        assertThat(PlaywrightPageRegistry.hasRegisteredPages()).isFalse();

        Page mockPage = mock(Page.class);
        PlaywrightPageRegistry.registerPage(mockPage);

        assertThat(PlaywrightPageRegistry.hasRegisteredPages()).isTrue();
    }

    @Test
    void shouldReturnUnmodifiableList() {
        Page mockPage = mock(Page.class);
        PlaywrightPageRegistry.registerPage(mockPage);

        List<Page> pages = PlaywrightPageRegistry.getRegisteredPages();

        org.junit.jupiter.api.Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> pages.add(mock(Page.class))
        );
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        // Each thread registers its own page
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Page mockPage = mock(Page.class);
                    PlaywrightPageRegistry.registerPage(mockPage);

                    // Verify we can read pages
                    assertThat(PlaywrightPageRegistry.getRegisteredPages()).isNotNull();

                    // Clear only affects current thread
                    PlaywrightPageRegistry.clear();
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(exceptions).isEmpty();
    }

    @Test
    void shouldIsolatePagesPerThread() throws InterruptedException {
        Page mainThreadPage = mock(Page.class);
        PlaywrightPageRegistry.registerPage(mainThreadPage);

        List<Page> otherThreadPages = Collections.synchronizedList(new ArrayList<>());

        Thread otherThread = new Thread(() -> {
            // This thread should have its own registry
            Page otherPage = mock(Page.class);
            PlaywrightPageRegistry.registerPage(otherPage);
            otherThreadPages.addAll(PlaywrightPageRegistry.getRegisteredPages());
        });

        otherThread.start();
        otherThread.join(5000);

        // Main thread should only see its own page
        assertThat(PlaywrightPageRegistry.getRegisteredPages())
            .containsExactly(mainThreadPage);

        // Other thread should only have seen its own page
        assertThat(otherThreadPages).hasSize(1);
        assertThat(otherThreadPages).doesNotContain(mainThreadPage);
    }
}
