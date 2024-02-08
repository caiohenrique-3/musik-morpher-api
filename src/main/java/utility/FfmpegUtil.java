package utility;

import model.AudioFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FfmpegUtil {
    public static void processFile(AudioFile file, String slowed) {
        double asetRateModifier = 0;
        double atempo = 0;

        if (slowed != null && slowed.equals("true")) {
            asetRateModifier = 0.9;
            atempo = 1.0;
        } else {
            asetRateModifier = 1.25;
            atempo = 1.06;
        }

        modifyAudioFile(file, asetRateModifier, atempo);

        file.setFileCode("/download/" + file.getFileCode());
    }

    private static void modifyAudioFile(AudioFile file,
                                        double asetRateModifier,
                                        double atempo) {
        String songPath = Paths.get("user-uploaded-files", file.getFileCode() +
                "-" + file.getFileName()).toAbsolutePath().toString();

        executeRuntimeCommand(songPath, asetRateModifier, atempo);
    }

    private static void executeRuntimeCommand(
            String songPath, double asetRateModifier,
            double atempo) {
        try {
            Path songParentDirectory =
                    Paths.get(songPath).getParent().toAbsolutePath();

            List<String> commands =
                    createFfmpegCommandBody(songPath, asetRateModifier, atempo);

            setupAndStartFfmpegProcess(songParentDirectory, commands);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> createFfmpegCommandBody(
            String songPath,
            double asetRateModifier, double atempo) {
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

    private static void setupAndStartFfmpegProcess(
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

    private static void replaceOldFileWithFfmpegOutput(List<String> commands) {
        File originalFile = new File(commands.get(3));
        File tempFile = new File(commands.get(commands.size() - 1));

        if (originalFile.delete() && tempFile.renameTo(originalFile)) {
            System.out.println("[FFMPEG]: File replaced successfully");
        } else {
            System.out.println("[FFMPEG]: Failed to replace file");
        }
    }
}
