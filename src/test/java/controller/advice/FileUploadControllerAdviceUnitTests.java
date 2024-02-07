package controller.advice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUploadControllerAdviceUnitTests {
    @InjectMocks
    private FileUploadControllerAdvice fileUploadControllerAdvice;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleIoException() {
        ResponseEntity<String> response = fileUploadControllerAdvice.handleIoException();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error ocurred when trying to process your file. Please try again.", response.getBody());
    }

    @Test
    public void testHandleUploadedFileIsTooBigException() {
        ResponseEntity<String> response = fileUploadControllerAdvice.handleUploadedFileIsTooBigException();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is too big. Maximum allowed file size is 15MB.", response.getBody());
    }
}
