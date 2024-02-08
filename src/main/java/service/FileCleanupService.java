package service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class FileCleanupService {
    private static final long FILE_MAX_AGE = TimeUnit.MINUTES.toMillis(5);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = Logger.getLogger(FileCleanupService.class.getName());

    @PostConstruct
    public void start() {
        scheduler.scheduleAtFixedRate(this::deleteOldFiles, 0, 5, TimeUnit.MINUTES);
    }

    private void deleteOldFiles() {
        long startTime = System.currentTimeMillis();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("user-uploaded-files"))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path) && isFileOld(path)) {
                    logger.info("Deleting file: " + path);
                    Files.delete(path);
                }
            }
        } catch (IOException e) {
            logger.severe("An error occurred: " + e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        long minutesUntilNextCleanup = 5 - TimeUnit.MILLISECONDS.toMinutes(duration);
        logger.info("Next cleanup in: " + minutesUntilNextCleanup + " minutes");
    }

    private boolean isFileOld(Path path) throws IOException {
        return Files.getLastModifiedTime(path).toMillis() < System.currentTimeMillis() - FILE_MAX_AGE;
    }
}