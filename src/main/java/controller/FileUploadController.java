package controller;

import model.AudioFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Code for upload & download
// https://www.codejava.net/frameworks/spring-boot/file-download-upload-rest-api-examples

@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public ResponseEntity<AudioFile> handleFileUpload(@RequestParam("file") MultipartFile file)
            throws IOException {
        AudioFile userUploadedFile = AudioFile.createFromMultipartFile(file);
        AudioFile processedFile = processFileWithFfmpeg(userUploadedFile);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(processedFile);
    }


    private AudioFile processFileWithFfmpeg(AudioFile file) {
        AudioFile response = new AudioFile();

        // Logic with ffmpeg here...

        response.setFileName(file.getFileName());
        response.setSize(file.getSize());
        response.setFileCode("/download/" + file.getFileCode());
        return response;
    }
}
