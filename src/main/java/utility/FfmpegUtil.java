package utility;

import model.AudioFile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class FfmpegUtil {
    private static final double DEFAULT_ASETRATE_MODIFIER = 1.25;
    private static final double DEFAULT_ATEMPO = 1.06;
    private static final double SLOWED_ASETRATE_MODIFIER = 0.91;
    private static final double SLOWED_ATEMPO = 1.0;

    private double asetRateModifier;
    private double atempo;

    public FfmpegUtil() {
        this.asetRateModifier = DEFAULT_ASETRATE_MODIFIER;
        this.atempo = DEFAULT_ATEMPO;
    }

    public void processFile(AudioFile file, String slowed) {
        asetRateModifier = DEFAULT_ASETRATE_MODIFIER;
        atempo = DEFAULT_ATEMPO;

        if (slowed != null && slowed.equals("true")) {
            asetRateModifier = SLOWED_ASETRATE_MODIFIER;
            atempo = SLOWED_ATEMPO;
        }

        modifyAudioFile(file);

        file.setFileCode("/download/" + file.getFileCode());
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
