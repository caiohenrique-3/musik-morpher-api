package utility;

import model.AudioFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileDownloadUtilUnitTests {
    private String randomFileName;

    @BeforeEach
    void setUpRandomFileName() {
        randomFileName = RandomStringUtils.randomAlphanumeric(12);
    }

    @Test
    void givenHappyPath_whenPrepareFileForDownload_thenResponse() throws IOException {
        String fileName = randomFileName + ".mp3";

        MultipartFile multipartFile = mock(MultipartFile.class);
        doReturn("file").when(multipartFile).getName();
        doReturn(fileName).when(multipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(multipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(multipartFile).getInputStream();

        AudioFile audioFile = AudioFile.createFromMultipartFile(multipartFile);

        assertInstanceOf(ResponseEntity.class, FileDownloadUtil
                .prepareFileForDownload(audioFile.getFileCode()));
    }

    @Test
    void givenHappyPath_whenPrepareFileForDownload_thenNotNull() throws IOException {
        String fileName = randomFileName + ".mp3";

        MultipartFile multipartFile = mock(MultipartFile.class);
        doReturn("file").when(multipartFile).getName();
        doReturn(fileName).when(multipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(multipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(multipartFile).getInputStream();

        AudioFile audioFile = AudioFile.createFromMultipartFile(multipartFile);

        assertNotEquals(null,
                FileDownloadUtil
                        .prepareFileForDownload(audioFile.getFileCode()));
    }

    @Test
    void givenFailure_whenPrepareFileForDownload_thenIoException() throws IOException {
        try (MockedStatic<FileDownloadUtil> classMock = mockStatic(FileDownloadUtil.class)) {

            classMock.when(() -> FileDownloadUtil
                    .prepareFileForDownload(anyString())).thenThrow(new IOException("Test exception"));

            assertThrows(IOException.class, () ->
                    FileDownloadUtil.prepareFileForDownload("f1l3c0d3"));
        }
    }

    @Test
    void givenBadCode_whenPrepareFileForDownload_thenNotFoundException() throws IOException {
        String bigString = "f1l3c0d3aoksdkoasdqldp120239120çq";
        assertThrows(controller.exceptions.FileNotFoundException.class,
                () -> FileDownloadUtil
                        .prepareFileForDownload(bigString));
    }
}
