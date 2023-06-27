package net.thucydides.core.reports.io;

import net.serenitybdd.core.time.SystemClock;
import net.thucydides.core.guice.Injectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SafelyMoveFiles {

    public static final int DELAY_BETWEEN_FILE_ACCESSES_IN_MS = 500;
    private final Path origin;
    private final int maxRetries;
    private final SystemClock clock;

    public SafelyMoveFiles(Path origin, int maxRetries) {
        this.origin = origin;
        this.maxRetries = maxRetries;
        this.clock = Injectors.getInjector().getInstance(net.serenitybdd.core.time.SystemClock.class);
    }

    public static SafelyMoveFilesBuilder withMaxRetriesOf(int maxRetries) {
        return new SafelyMoveFilesBuilder(maxRetries);
    }

    public Path to(Path destination) throws IOException {
        try {
            if (Files.exists(destination)) {
                return destination;
            }
            return Files.move(origin,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (NoSuchFileException fileAlreadyMoved) {
            return destination;
        } catch (IOException e) {
            if (maxRetries == 0) { throw e; }

            clock.pauseFor(DELAY_BETWEEN_FILE_ACCESSES_IN_MS);
            return SafelyMoveFiles.withMaxRetriesOf(maxRetries - 1).from(origin).to(destination);
        }
    }

    public static class SafelyMoveFilesBuilder {
        private final int maxRetries;

        public SafelyMoveFilesBuilder(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        public SafelyMoveFiles from(Path origin) {
            return new SafelyMoveFiles(origin, maxRetries);
        }
    }
}
