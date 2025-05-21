package utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class WhisperJsonReader {
	public static List<WhisperSegment> readSegments(String jsonPath) {
        try (FileReader reader = new FileReader(jsonPath)) {
            Gson gson = new Gson();
            Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
            String segmentJson = gson.toJson(map.get("segments"));
            return gson.fromJson(segmentJson, new TypeToken<List<WhisperSegment>>(){}.getType());
        } catch (Exception e) {
            System.out.println("❌ Lỗi đọc JSON: " + e.getMessage());
            return null;
        }
    }
}
