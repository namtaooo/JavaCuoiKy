package database;
import java.sql.*;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class Mp3FileDAO {
	public static void insert(Mp3File file) {
        String sql = "INSERT INTO Mp3File (file_name, file_path, duration, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, file.getFileName());
            pstmt.setString(2, file.getFilePath());
            pstmt.setDouble(3, file.getDuration());
            pstmt.setString(4, file.getCreatedAt());
            pstmt.executeUpdate();
            System.out.println("✅ Thêm mp3 vào DB thành công.");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi insert Mp3File: " + e.getMessage());
        }
    }

    public static List<Mp3File> getAll() {
        List<Mp3File> list = new ArrayList<>();
        String sql = "SELECT * FROM Mp3File";
        try (Connection conn = SQLiteConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mp3File file = new Mp3File(
                    rs.getInt("id"),
                    rs.getString("file_name"),
                    rs.getString("file_path"),
                    rs.getDouble("duration"),
                    rs.getString("created_at")
                );
                list.add(file);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getAll Mp3File: " + e.getMessage());
        }
        return list;
    }
    public static Mp3File findByName(String name) {
        String sql = "SELECT * FROM Mp3File WHERE file_name = ?";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Mp3File(
                    rs.getInt("id"),
                    rs.getString("file_name"),
                    rs.getString("file_path"),
                    rs.getDouble("duration"),
                    rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi findByName: " + e.getMessage());
        }
        return null;
    }

}
