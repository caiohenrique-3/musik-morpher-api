package controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice(basePackageClasses =
        {controller.FileDownloadController.class})
public class FileDownloadControllerAdvice {
    @ExceptionHandler({controller.exceptions.FileNotFoundException.class})
    public ResponseEntity<String> handleFileNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("File not found");
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<String> handleIoException() {
        return ResponseEntity
                .internalServerError()
                .body("An error ocurred when trying to prepare your file for download." +
                        " Please try again.");
    }
}
