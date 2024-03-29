package controller;


import model.AudioFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;
import utility.FfmpegUtil;
import utility.FileDownloadUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {main.Main.class})
@AutoConfigureMockMvc
public class FileDownloadControllerUnitTests {
    private static final String END_POINT_PATH = "/download/";
    private String randomFileName;

    @BeforeEach
    void setUpRandomFileName() {
        randomFileName = RandomStringUtils.randomAlphanumeric(12);
    }

    @Autowired
    private MockMvc mvc;

    @Test
    void givenFoundFile_whenDownloadGetRequest_thenFile() throws Exception {
        String fileCode = "t3stc0d3";
        String fileName = randomFileName + ".mp3";

        // Mocks
        Resource fileToBeDownloaded = mock(Resource.class);
        doReturn(fileName).when(fileToBeDownloaded).getFilename();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));

        ResponseEntity<Resource> responseToUser = mock(ResponseEntity.class);
        doReturn(fileToBeDownloaded).when(responseToUser).getBody();
        doReturn(HttpStatus.OK).when(responseToUser).getStatusCode();
        doReturn(headers).when(responseToUser).getHeaders();

        try (MockedStatic<FileDownloadUtil> mockedFileDownloadUtil = mockStatic(FileDownloadUtil.class)) {
            mockedFileDownloadUtil
                    .when(() -> FileDownloadUtil.prepareFileForDownload(fileCode))
                    .thenReturn(responseToUser);

            // Request
            MvcResult result = mvc.perform(get(END_POINT_PATH + fileCode)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Verify the response
            MockHttpServletResponse response = result.getResponse();
            assertEquals("application/octet-stream", response.getContentType());
            assertEquals("attachment; filename=\"" + fileName + "\"", response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        }
    }

    @Test
    void givenCantFindFile_whenDownloadGetRequest_then404NotFound() throws Exception {
        mvc.perform(get(END_POINT_PATH + 12)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenIOException_whenDownloadGetRequest_then500InternalServerError() throws Exception {
        String fileName = randomFileName + ".mp3";

        MultipartFile multipartFile = mock(MultipartFile.class);
        doReturn("file").when(multipartFile).getName();
        doReturn(fileName).when(multipartFile).getOriginalFilename();
        doReturn("audio/mpeg").when(multipartFile).getContentType();
        doReturn(new ByteArrayInputStream(new byte[0]))
                .when(multipartFile).getInputStream();

        AudioFile audioFile = AudioFile
                .createFromMultipartFile(multipartFile);

        try (MockedStatic<FileDownloadUtil> mockedFileDownloadUtil =
                     mockStatic(FileDownloadUtil.class)) {
            mockedFileDownloadUtil
                    .when(() -> FileDownloadUtil
                            .prepareFileForDownload(anyString()))
                    .thenThrow(new IOException("Test exception"));

            mvc.perform(get(END_POINT_PATH +
                            audioFile.getFileCode())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }

    }
}
