package utility;

import model.AudioFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class FfmpegUtilUnitTests {
    private String randomFileName;

    @BeforeEach
    void setUpRandomFileName() {
        randomFileName = RandomStringUtils.randomAlphanumeric(12);
    }

    @Test
    void givenHappyPath_whenProcessFile_thenDownloadUrl() throws IOException {
        String fileName = randomFileName + ".mp3";
        String fileCode = "t3stc0d3";

        AudioFile audioFile = new AudioFile();
        audioFile.setFileCode(fileCode);
        audioFile.setFileName(fileName);
        audioFile.setSize(1.0);

        FfmpegUtil ffmpegUtil = new FfmpegUtil();

        ffmpegUtil.processFile(audioFile, "false");

        assertTrue(audioFile.getFileCode().startsWith("/download/"));
    }
}
