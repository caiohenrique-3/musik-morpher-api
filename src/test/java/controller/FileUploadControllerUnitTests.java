package controller;

import jakarta.servlet.ServletException;
import model.AudioFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import utility.FileUploadUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {main.Main.class})
@AutoConfigureMockMvc
public class FileUploadControllerUnitTests {
    private static final String END_POINT_PATH = "/upload";
    private static final Path SAMPLE_SONG = Paths.get("src/test/sample.mp3");
    private static final byte[] SAMPLE_SONG_CONTENT = new byte[0];

    private String randomFileName;

    @BeforeEach
    void setUpRandomFileName() {
        randomFileName = RandomStringUtils.randomAlphanumeric(12);
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void givenNoMultipartFile_whenPostRequest_thenThrowException()
            throws Exception {
        assertThrows(ServletException.class, () -> {
            mvc.perform(post(END_POINT_PATH)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        });
    }

    @Test
    void givenNotAnAudioFile_whenPostFile_thenBadRequest()
            throws Exception {
        MockMultipartFile mockImageFile = new MockMultipartFile(
                "file",
                randomFileName + ".png",
                MediaType.IMAGE_PNG_VALUE,
                SAMPLE_SONG_CONTENT);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));
    }

    @Test
    void givenAudioFile_whenPostFile_thenStatusOk()
            throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenAudioFileAndParameters_whenPostFile_thenStatusOk()
            throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockMultipartFile)
                        .param("slowed", "true")
                        .contentType("audio/mpeg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenAudioFileAndParameterSlowedEqualsFalse_whenPostFile_thenStatusOk()
            throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockMultipartFile)
                        .param("slowed", "false")
                        .contentType("audio/mpeg"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test()
    void givenCreateFromMultipartFileSucceeds_whenPostFile_thenReturnAudioFile()
            throws Exception {
        // Mock for the post request
        MockMultipartFile mockUploadedFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        // Mock for the static method
        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        doReturn(mockUploadedFile.getName()).when(mockMultipartFile).getName();
        doReturn(mockUploadedFile.getOriginalFilename()).when(mockMultipartFile).getOriginalFilename();
        doReturn(mockUploadedFile.getContentType()).when(mockMultipartFile).getContentType();
        doReturn(mockUploadedFile.getInputStream()).when(mockMultipartFile).getInputStream();

        // Static method mock
        try (MockedStatic<AudioFile> audioFileMockedStatic =
                     mockStatic(AudioFile.class, CALLS_REAL_METHODS)) {
            audioFileMockedStatic
                    .when(() -> AudioFile.createFromMultipartFile(mockMultipartFile))
                    .thenReturn(any(AudioFile.class));

            // Post request
            mvc.perform(multipart(END_POINT_PATH)
                            .file(mockUploadedFile)
                            .param("slowed", "false")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }

    @Test()
    void givenCreateFromMultipartFileFails_whenPostFile_thenInternalServerError()
            throws Exception {
        MockMultipartFile mockUploadedFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        try (MockedStatic<AudioFile> audioFileMockedStatic =
                     mockStatic(AudioFile.class, CALLS_REAL_METHODS)) {
            audioFileMockedStatic
                    .when(() -> AudioFile.createFromMultipartFile(any(MultipartFile.class)))
                    .thenThrow(IOException.class);

            mvc.perform(multipart(END_POINT_PATH)
                            .file(mockUploadedFile)
                            .param("slowed", "false")
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
        }
    }

    @Test
    void givenFileBiggerThan15MB_whenPostFile_thenBadRequest() throws Exception {
        MockMultipartFile mockUploadedFile = new MockMultipartFile(
                "file",
                randomFileName + ".mp3",
                "audio/mpeg",
                SAMPLE_SONG_CONTENT);

        MockedStatic<FileUploadUtil> fileUploadUtilMockedStatic =
                mockStatic(FileUploadUtil.class, CALLS_REAL_METHODS);

        fileUploadUtilMockedStatic
                .when(() -> FileUploadUtil.isAllowedFileSize(any(MultipartFile.class)))
                .thenThrow(controller.exceptions.UploadedFileIsTooBigException.class);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockUploadedFile)
                        .param("slowed", "false")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
}
