package utility;

import model.AudioFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.io.Resource;
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
    void givenHappyPath_whenGetFileAsResource_thenUrlResource() throws IOException {
        String fileName = randomFileName + ".mp3";

        MultipartFile multipartFile = mock(MultipartFile.class);
        doReturn("file").when(multipartFile).getName();
        doReturn(fileName).when(multipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(multipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(multipartFile).getInputStream();

        AudioFile audioFile = AudioFile.createFromMultipartFile(multipartFile);

        assertInstanceOf(Resource.class, FileDownloadUtil
                .getFileAsResource(audioFile.getFileCode()));
    }

    @Test
    void givenHappyPath_whenGetFileAsResource_thenNotNull() throws IOException {
        String fileName = randomFileName + ".mp3";

        MultipartFile multipartFile = mock(MultipartFile.class);
        doReturn("file").when(multipartFile).getName();
        doReturn(fileName).when(multipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(multipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(multipartFile).getInputStream();

        AudioFile audioFile = AudioFile.createFromMultipartFile(multipartFile);

        assertNotEquals(null,
                FileDownloadUtil.getFileAsResource(audioFile.getFileCode()));
    }

    @Test
    void givenFailure_whenGetFileAsResource_thenIoException() throws IOException {
        try (MockedStatic<FileDownloadUtil> classMock = mockStatic(FileDownloadUtil.class)) {

            classMock.when(() -> FileDownloadUtil.getFileAsResource(anyString())).thenThrow(new IOException("Test exception"));

            assertThrows(IOException.class, () ->
                    FileDownloadUtil.getFileAsResource("f1l3c0d3"));
        }
    }

    @Test
    void givenBadCode_whenGetFileAsResource_thenNull() throws IOException {
        String bigString = "f1l3c0d3aoksdkoasdqldp120239120çq";
        assertEquals(null, FileDownloadUtil
                .getFileAsResource(bigString));
    }
}
