package model;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import utility.FileUploadUtil;

import java.io.IOException;

public class AudioFile {
    private String fileName;
    private String fileCode;
    private double size;

    public static AudioFile createFromMultipartFile(MultipartFile file) throws IOException {
        AudioFile audioFile = new AudioFile();
        setFileAttributes(file, audioFile);
        return audioFile;
    }

    private static void setFileAttributes(MultipartFile file, AudioFile audioFile) throws IOException {
        String fileName = getCleanFileName(file);
        String fileCode = FileUploadUtil.saveFileToDiskAndGetUniqueCode(fileName, file);
        double size = getRoundedFileSizeInMegabytes(file);

        audioFile.setFileName(fileName);
        audioFile.setFileCode(fileCode);
        audioFile.setSize(size);
    }

    private static String getCleanFileName(MultipartFile file) {
        return StringUtils.cleanPath(file.getOriginalFilename());
    }

    private static double getRoundedFileSizeInMegabytes(MultipartFile file) {
        long fileSizeInBytes = file.getSize();
        double fileSizeInMegabytes = fileSizeInBytes / (1024.0 * 1024.0);
        fileSizeInMegabytes = Math.round(fileSizeInMegabytes * 100.0) / 100.0;
        return fileSizeInMegabytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileCode() {
        return fileCode;
    }

    public void setFileCode(String fileCode) {
        this.fileCode = fileCode;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
