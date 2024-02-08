package utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileUploadUtilUnitTests {
    private static final Path SAMPLE_SONG = Paths.get("src/test/sample.mp3");
    private static final byte[] SAMPLE_SONG_CONTENT;

    static {
        try {
            SAMPLE_SONG_CONTENT = Files.readAllBytes(SAMPLE_SONG);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String randomFileName;

    @BeforeEach
    void setUpRandomFileName() {
        randomFileName = RandomStringUtils.randomAlphanumeric(12);
    }

    @Test
    void givenGoodFile_whenCallingIsAudioFile_thenTrue() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn("test.mp3").when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doReturn(new ByteArrayInputStream(SAMPLE_SONG_CONTENT))
                .when(mockMultipartFile).getInputStream();

        assertEquals(true, FileUploadUtil.isAudioFile(mockMultipartFile));
    }

    @Test
    void givenBadFile_whenCallingIsAudioFile_thenFalse() throws IOException {
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn("test.png").when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doReturn(new ByteArrayInputStream(SAMPLE_SONG_CONTENT))
                .when(mockMultipartFile).getInputStream();

        assertEquals(false, FileUploadUtil.isAudioFile(mockMultipartFile));
    }

    @Test
    void givenHappyPath_whenSavingFileToDisk_thenUniqueFileCode() throws IOException {
        String multipartFileName = randomFileName + ".mp3";
        String cleanFileName = StringUtils.cleanPath(multipartFileName);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn(multipartFileName).when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doReturn(new ByteArrayInputStream(SAMPLE_SONG_CONTENT))
                .when(mockMultipartFile).getInputStream();

        assertNotEquals(null, FileUploadUtil
                .saveFileToDiskAndGetUniqueCode(cleanFileName, mockMultipartFile));
    }

    @Test
    void givenBadPath_whenSavingFileToDisk_thenIoException() throws IOException {
        String multipartFileName = randomFileName + ".mp3";
        String cleanFileName = StringUtils.cleanPath(multipartFileName);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn(multipartFileName).when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doThrow(new IOException("Test exception")).when(mockMultipartFile).getInputStream();

        assertThrows(IOException.class, () -> FileUploadUtil
                .saveFileToDiskAndGetUniqueCode(cleanFileName, mockMultipartFile));
    }

    @Test
    void givenFileWithin15Mb_whenCallingIsAllowedSize_thenTrue() throws IOException {
        String multipartFileName = randomFileName + ".mp3";
        String cleanFileName = StringUtils.cleanPath(multipartFileName);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn(multipartFileName).when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doReturn(new ByteArrayInputStream(SAMPLE_SONG_CONTENT))
                .when(mockMultipartFile).getInputStream();

        assertEquals(true, FileUploadUtil.isAllowedFileSize(mockMultipartFile));
    }

    @Test
    void givenFileBiggerThan15Mb_whenCallingIsAllowedSize_thenException() throws IOException {
        String multipartFileName = randomFileName + ".mp3";
        long sizeInBytes = 16L * 1024 * 1024;

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn("file").when(mockMultipartFile).getName();
        doReturn(multipartFileName).when(mockMultipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(mockMultipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(mockMultipartFile).getInputStream();

        doReturn(sizeInBytes).when(mockMultipartFile).getSize();

        assertThrows(
                controller.exceptions.UploadedFileIsTooBigException.class,
                () -> FileUploadUtil.isAllowedFileSize(mockMultipartFile));
    }
}
