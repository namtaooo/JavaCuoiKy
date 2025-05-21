package database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.*;
public class MergeFileDAO {
    public static void insert(MergeFile file) {
        try (Connection conn =SQLiteConnection.connect() ) {
            String sql = "INSERT INTO MergeFile (fileName, outputPath, createdAt) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, file.getFilename());
            stmt.setString(2, file.getOutputpath());
            stmt.setString(3, file.getCreateAt());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	public static List<MergeFile> getAll() {
        List<MergeFile> list = new ArrayList<>();
        try (Connection conn = SQLiteConnection.connect()) {
            String sql = "SELECT * FROM MergedAudioFile ORDER BY id DESC";
            ResultSet rs = conn.prepareStatement(sql).executeQuery();
            while (rs.next()) {
                MergeFile file = new MergeFile();
                file.setId(rs.getInt("id"));
                file.setFilename(rs.getString("fileName"));
                file.setOutputpath(rs.getString("outputPath"));
                file.setCreateAt(rs.getString("createdAt"));
                list.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
