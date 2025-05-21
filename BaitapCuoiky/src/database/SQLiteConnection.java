package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
	public static Connection connect() {
		Connection conn = null;
        try {
            String dbName = "audio_app.db";
            String dbPath = new File(dbName).getAbsolutePath();
            System.out.println("📌 Đang dùng DB: " + dbPath);

            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            System.out.println("✅ Kết nối SQLite thành công.");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi kết nối: " + e.getMessage());
        }
        return conn;
	}

}
