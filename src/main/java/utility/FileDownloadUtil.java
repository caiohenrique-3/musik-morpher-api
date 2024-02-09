package utility;

import controller.exceptions.FileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileDownloadUtil {
    public static ResponseEntity<Resource> prepareFileForDownload(String fileCode)
            throws IOException {
        Resource resource = null;

        resource = getFileAsResource(fileCode);

        if (resource == null) {
            throw new FileNotFoundException();
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\""
                + resource.getFilename() + "\"";

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }

    private static Resource getFileAsResource(String fileCode) throws IOException {
        Path dirPath = Paths.get("user-uploaded-files");

        Path foundFile = findFileByCode(dirPath, fileCode);

        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }

        return null;
    }

    private static Path findFileByCode(Path dirPath, String fileCode) throws IOException {
        List<Path> files = getAllFiles(dirPath);

        for (Path file : files) {
            if (file.getFileName().toString().startsWith(fileCode))
                return file;
        }

        return null;
    }

    private static List<Path> getAllFiles(Path dirPath) throws IOException {
        List<Path> files = new ArrayList<>();

        try (var filesStream = Files.list(dirPath)) {
            files = filesStream.collect(Collectors.toList());
        }

        return files;
    }
}