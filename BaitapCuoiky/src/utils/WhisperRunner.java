package utils;


import java.io.IOException;

public class WhisperRunner {
    public static boolean runWhisper(String mp3Path, String outputDir) {
        ProcessBuilder builder = new ProcessBuilder(
            "python", "-m", "whisper", mp3Path,
            "--language", "Vietnamese",
            "--output_format", "json",
            "--output_dir", outputDir,
            "--device", "cpu",
            "--model", "tiny"
        );
        String ffmpegPath = "C:\\ffmpeg\\bin"; // sửa theo thư mục bạn đã cài
        builder.environment().put("PATH", ffmpegPath + ";" + System.getenv("PATH"));
        builder.environment().put("PYTHONIOENCODING", "utf-8"); 

        builder.redirectErrorStream(true); 
        try {
            Process process = builder.inheritIO().start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            System.out.println("❌ Lỗi khi gọi Whisper: " + e.getMessage());
            return false;
        }
    }
}

