package controller;

import model.AudioFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import utility.FfmpegUtil;
import utility.FileUploadUtil;

// Code for upload & download
// https://www.codejava.net/frameworks/spring-boot/file-download-upload-rest-api-examples

@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "slowed", required = false) String slowed)
            throws IOException {
        if (FileUploadUtil.isAudioFile(file)
                && FileUploadUtil.isAllowedFileSize(file)) {
            AudioFile userUploadedFile = AudioFile.createFromMultipartFile(file);

            AudioFile processedFile = FfmpegUtil.processFile(userUploadedFile, slowed);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(processedFile);
        }

        return ResponseEntity
                .badRequest()
                .body("Invalid file type. Please upload an audio file.");
    }
}