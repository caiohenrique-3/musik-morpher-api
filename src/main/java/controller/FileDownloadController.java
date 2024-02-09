package controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import utility.FileDownloadUtil;

import java.io.IOException;

@RestController
public class FileDownloadController {
    @GetMapping("/download/{fileCode}")
    public ResponseEntity<?> downloadFile(
            @PathVariable("fileCode") String fileCode) throws IOException {

        ResponseEntity<Resource> response =
                FileDownloadUtil.prepareFileForDownload(fileCode);

        return response;
    }
}
