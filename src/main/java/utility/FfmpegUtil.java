package utility;

import model.AudioFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FfmpegUtil {
    static Logger logger =
            Logger.getLogger(FfmpegUtil.class.getName());

    public static void processFile(AudioFile file, String slowed) {
        if (slowed != null && slowed.equals("true"))
            slowDown(file);
        else
            nightcore(file);

        String fileCode = file.getFileCode();
        file.setFileCode("/download/" + fileCode);
    }

    private static void slowDown(AudioFile file) {
        String songPath = Paths.get("user-uploaded-files", file.getFileCode() +
                "-" + file.getFileName()).toAbsolutePath().toString();

        double asetRateModifier = 0.9;

        executeRuntimeCommand(songPath, asetRateModifier);
    }

    private static void nightcore(AudioFile file) {
        String songPath = Paths.get("user-uploaded-files", file.getFileCode() +
                "-" + file.getFileName()).toAbsolutePath().toString();

        double asetRateModifier = 1.25;

        executeRuntimeCommand(songPath, asetRateModifier);
    }

    private static void executeRuntimeCommand(String songPath, double asetRateModifier) {
        try {
            Path songParentDirectory =
                    Paths.get(songPath).getParent().toAbsolutePath();

            List<String> commands =
                    createFfmpegCommandBody(songPath, asetRateModifier);

            setupAndStartFfmpegProcess(songParentDirectory, commands);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> createFfmpegCommandBody(
            String songPath, double asetRateModifier) {
        List<String> commandBody = new ArrayList<>();

        commandBody.add("ffmpeg");
        commandBody.add("-y");
        commandBody.add("-i");
        commandBody.add(songPath);
        commandBody.add("-filter:a");
        commandBody.add("atempo=1.0,asetrate=44100*" + asetRateModifier);
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
            File originalFile = new File(commands.get(3));
            File tempFile = new File(commands.get(commands.size() - 1));

            if (originalFile.delete() && tempFile.renameTo(originalFile)) {
                System.out.println("[FFMPEG]: File replaced successfully");
            } else {
                System.out.println("[FFMPEG]: Failed to replace file");
            }

            System.out.println("[FFMPEG]: Success");
        } else {
            System.out.println("[FFMPEG]: Failed with exit code " + exitCode);
        }
    }
}
