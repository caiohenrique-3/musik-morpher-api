package controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice(basePackageClasses =
        {controller.FileUploadController.class})
public class FileUploadControllerAdvice {
    @ExceptionHandler({IOException.class, InterruptedException.class})
    public ResponseEntity<String> handleIoException() {
        return ResponseEntity
                .internalServerError()
                .body("An error ocurred when trying to process your file. Please try again.");
    }

    @ExceptionHandler(controller.exceptions.UploadedFileIsTooBigException.class)
    public ResponseEntity<String> handleUploadedFileIsTooBigException() {
        return ResponseEntity
                .badRequest()
                .body("File is too big. Maximum allowed file size is 15MB.");
    }
}
