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

@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String slowed)
            throws IOException {
        if (FileUploadUtil.isValidFile(file)) {
            AudioFile userUploadedFile = AudioFile.createFromMultipartFile(file);

            FfmpegUtil.processFile(userUploadedFile, slowed);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(userUploadedFile);
        }

        return ResponseEntity
                .badRequest()
                .body("Invalid file type. Please upload an audio file.");
    }
}