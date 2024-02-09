package utility;

import model.AudioFile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Since the class is now a bean in Spring, the values don't change between requests
// This means if the first request has slowed = "true", it will change the values in the bean itself,
// causing it to affect the next requests too, because asetRate and atempo will have the slowed values now.
// TODO: Make Spring create & destroy a Bean of this type on each request.

@Component
public class FfmpegUtil {
    private double asetRateModifier;
    private double atempo;

    public FfmpegUtil() {
        this.asetRateModifier = 1.25;
        this.atempo = 1.06;
    }

    public void processFile(AudioFile file, String slowed) {
        if (slowed != null && slowed.equals("true")) {
            asetRateModifier = 0.91;
            atempo = 1.0;
        }

        modifyAudioFile(file);

        file.setFileCode("/download/" + file.getFileCode());

        resetValuesToDefaultAfterProcessing();
    }

    private void modifyAudioFile(AudioFile file) {
        String songPath = Paths.get("user-uploaded-files", file.getFileCode() +
                "-" + file.getFileName()).toAbsolutePath().toString();

        executeRuntimeCommand(songPath);
    }

    private void executeRuntimeCommand(
            String songPath) {
        try {
            Path songParentDirectory =
                    Paths.get(songPath).getParent().toAbsolutePath();

            List<String> commands =
                    createFfmpegCommandBody(songPath);

            setupAndStartFfmpegProcess(songParentDirectory, commands);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<String> createFfmpegCommandBody(
            String songPath) {
        List<String> commandBody = new ArrayList<>();

        commandBody.add("ffmpeg");
        commandBody.add("-y");
        commandBody.add("-i");
        commandBody.add(songPath);
        commandBody.add("-filter:a");
        commandBody.add("atempo=" + atempo + ",asetrate=44100*" + asetRateModifier);
        commandBody.add("-vn");
        commandBody.add("-map");
        commandBody.add("0");
        commandBody.add("-map_metadata");
        commandBody.add("0:g");

        File f = new File(songPath);
        String tempFilePath = f.getParent() + "/temp_" + f.getName();

        commandBody.add(tempFilePath);

        return commandBody;
    }

    private void setupAndStartFfmpegProcess(
            Path workingDirectory, List<String> commands)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            replaceOldFileWithFfmpegOutput(commands);

            System.out.println("[FFMPEG]: Success");
        } else {
            System.out.println("[FFMPEG]: Failed with exit code " + exitCode);
        }
    }

    private void replaceOldFileWithFfmpegOutput(List<String> commands) {
        File originalFile = new File(commands.get(3));
        File tempFile = new File(commands.get(commands.size() - 1));

        if (originalFile.delete() && tempFile.renameTo(originalFile)) {
            System.out.println("[FFMPEG]: File replaced successfully");
        } else {
            System.out.println("[FFMPEG]: Failed to replace file");
        }
    }

    private void resetValuesToDefaultAfterProcessing() {
        this.asetRateModifier = 1.25;
        this.atempo = 1.06;
    }

    public double getAsetRateModifier() {
        return asetRateModifier;
    }

    public void setAsetRateModifier(double asetRateModifier) {
        this.asetRateModifier = asetRateModifier;
    }

    public double getAtempo() {
        return atempo;
    }

    public void setAtempo(double atempo) {
        this.atempo = atempo;
    }
}
