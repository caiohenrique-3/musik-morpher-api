package utility;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
    public static boolean isAudioFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return StringUtils.getFilenameExtension(fileName).equals("mp3");
    }

    public static String saveFileToDiskAndGetUniqueCode(String fileName, MultipartFile file)
            throws IOException {
        Path uploadsFolder = createUploadedUserFilesFolder();
        String fileCode = RandomStringUtils.randomAlphanumeric(8);

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadsFolder.resolve(fileCode + "-" + fileName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save file: " + fileName, ioe);
        }

        return fileCode;
    }

    private static Path createUploadedUserFilesFolder() throws IOException {
        Path uploadsFolder = Paths.get("user-uploaded-files");

        if (!Files.exists(uploadsFolder)) {
            Files.createDirectories(uploadsFolder);
        }

        return uploadsFolder;
    }
}