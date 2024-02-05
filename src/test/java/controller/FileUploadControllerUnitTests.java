package controller;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {main.Main.class})
@AutoConfigureMockMvc
public class FileUploadControllerUnitTests {
    private static final String END_POINT_PATH = "/upload";

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
        byte[] fileContent = "This is the file content".getBytes();

        MockMultipartFile mockImageFile = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                fileContent);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE));

    }

    @Test
    void givenAudioFile_whenPostFile_thenStatusOk()
            throws Exception {
        byte[] mp3Content = "This is the file content".getBytes();

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                mp3Content);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void givenAudioFileAndParameters_whenPostFile_thenStatusOk()
            throws Exception {
        byte[] mp3Content = "This is the file content".getBytes();

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.mp3",
                "audio/mpeg",
                mp3Content);

        mvc.perform(multipart(END_POINT_PATH)
                        .file(mockMultipartFile)
                        .param("slowed", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
