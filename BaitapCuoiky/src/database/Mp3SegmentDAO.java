package database;

import java.sql.*;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class Mp3SegmentDAO {

    public static void insert(Mp3Segment seg) {
        String sql = "INSERT INTO Mp3Segment (mp3_file_id, start_time, end_time, text, is_verified) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seg.getMp3FileId());
            pstmt.setString(2, seg.getStartTime());
            pstmt.setString(3, seg.getEndTime());
            pstmt.setString(4, seg.getText());
            pstmt.setBoolean(5, seg.isVerified());
            pstmt.executeUpdate();
            System.out.println("✅ Thêm đoạn mp3 thành công.");
        } catch (SQLException e) {
            System.out.println("❌ Lỗi insert Mp3Segment: " + e.getMessage());
        }
    }

    public static List<Mp3Segment> getByFileId(int fileId) {
        List<Mp3Segment> list = new ArrayList<>();
        String sql = "SELECT * FROM Mp3Segment WHERE mp3_file_id = ?";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Mp3Segment seg = new Mp3Segment(
                    rs.getInt("id"),
                    rs.getInt("mp3_file_id"),
                    rs.getString("start_time"),
                    rs.getString("end_time"),
                    rs.getString("text"),
                    rs.getBoolean("is_verified")
                );
                list.add(seg);
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi getByFileId: " + e.getMessage());
        }
        return list;
    }
    public static void update(Mp3Segment segment) {
        String sql = "UPDATE Mp3Segment SET start_time = ?, end_time = ?, text = ?, is_sample = ? WHERE id = ?";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, segment.getStartTime());
            stmt.setString(2, segment.getEndTime());
            stmt.setString(3, segment.getText());
            stmt.setBoolean(4, segment.isVerified());
            stmt.setInt(5, segment.getId());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteByFileId(int fileId) {
        String sql = "DELETE FROM Mp3Segment WHERE mp3_file_id = ?";
        try (Connection conn = SQLiteConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, fileId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
