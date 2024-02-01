package controller;

import model.ProcessedFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utility.FileUploadUtil;

import java.io.IOException;

// Code for upload & download
// https://www.codejava.net/frameworks/spring-boot/file-download-upload-rest-api-examples

@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public ResponseEntity<ProcessedFile> handleFileUpload(@RequestParam("file") MultipartFile file)
            throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        double size = getRoundedFileSizeInMegabytes(file);

        String fileCode = FileUploadUtil.saveFile(fileName, file);

        ProcessedFile response = new ProcessedFile();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/download/" + fileCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    public double getRoundedFileSizeInMegabytes(MultipartFile file) {
        long fileSizeInBytes = file.getSize();
        double fileSizeInMegabytes = fileSizeInBytes / (1024.0 * 1024.0);
        fileSizeInMegabytes = Math.round(fileSizeInMegabytes * 100.0) / 100.0;
        return fileSizeInMegabytes;
    }
}
