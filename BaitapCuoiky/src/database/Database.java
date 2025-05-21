package database;
import java.sql.Connection;
import java.sql.Statement;

public class Database {
	public static void createTables() {
		String createMp3FileTable = """
            CREATE TABLE IF NOT EXISTS Mp3File (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                file_name TEXT NOT NULL,
                file_path TEXT NOT NULL,
                duration REAL,
                created_at TEXT
            );
            """;

        String createMp3SegmentTable = """
            CREATE TABLE IF NOT EXISTS Mp3Segment (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mp3_file_id INTEGER,
                start_time TEXT,
                end_time TEXT,
                text TEXT,
                is_verified BOOLEAN DEFAULT 0,
                FOREIGN KEY(mp3_file_id) REFERENCES Mp3File(id)
            );
            """;
        String createMergeFile = """
        		CREATE TABLE IF NOT EXISTS MergeFile (
				    id INTEGER PRIMARY KEY AUTOINCREMENT,
				    fileName TEXT NOT NULL,
				    outputPath TEXT NOT NULL,
				    createdAt TEXT NOT NULL
				);
        		""";

        try (Connection conn = SQLiteConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createMp3FileTable);
            stmt.execute(createMp3SegmentTable);
            stmt.execute(createMergeFile);
            System.out.println("✅ Tạo bảng thành công.");
        } catch (Exception e) {
            System.out.println("❌ Lỗi tạo bảng: " + e.getMessage());
        }
    }
}
