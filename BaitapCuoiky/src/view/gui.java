package view;

import javax.swing.*;
import model.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import database.Database;
import database.Mp3FileDAO;
import database.Mp3SegmentDAO;
import utils.WhisperRunner;

public class gui extends JFrame {
    private JTable lyricsTable;
    private JLabel selectedFileLabel;
    private String currentMp3Path;
    private DefaultTableModel tableModel;

    public gui() {
        setTitle("MP3 Editor Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(wrapper);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(55, 55, 55));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("<html><div style='text-align:center;'>👤<br>QUẢN LÝ MP3</div></html>", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        sidebar.add(menuPanel("Chọn file MP3", "📂", new Color(39, 174, 96), () -> chooseMp3()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(menuPanel("Trích lời (AI)", "🤖", new Color(39, 174, 96), () -> extractLyrics()));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(menuPanel("Cắt ghép file", "🤖", new Color(39, 174, 96), () ->{
        	new CutterFrame();
        }));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(menuPanel("Lưu chỉnh sửa", "💾", new Color(39, 174, 96), () -> saveEditedSegments()));

        JLabel footer = new JLabel(" nam", SwingConstants.CENTER);
        footer.setForeground(Color.LIGHT_GRAY);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(footer);

        JPanel fileInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileInfoPanel.setBackground(Color.WHITE);
        fileInfoPanel.setForeground(Color.BLUE);
        fileInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        selectedFileLabel = new JLabel("🎵 File đã chọn: Chưa chọn file");
        selectedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileInfoPanel.add(selectedFileLabel);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 206, 86));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Start", "End", "Text"};
		tableModel = new DefaultTableModel(columns, 0);
		lyricsTable = new JTable(tableModel);
//		TableColumnModel columnModel = lyricsTable.getColumnModel();
        lyricsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lyricsTable.setRowHeight(22);

        lyricsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        lyricsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        lyricsTable.getColumnModel().getColumn(2).setPreferredWidth(800);
        JScrollPane scrollPane = new JScrollPane(lyricsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        wrapper.add(sidebar, BorderLayout.WEST);
        wrapper.add(fileInfoPanel, BorderLayout.NORTH);
        wrapper.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel menuPanel(String text, String icon, Color color, Runnable action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel label = new JLabel(icon + "  " + text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label);

        if (action != null) {
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    action.run();
                }
            });
        }

        return panel;
    }

    private void chooseMp3() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentMp3Path = chooser.getSelectedFile().getAbsolutePath();
            selectedFileLabel.setText("🎵 File đã chọn: " + currentMp3Path);
        }
    }

    private void extractLyrics() {
    if (currentMp3Path == null || !currentMp3Path.endsWith(".mp3")) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn file .mp3 hợp lệ trước!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String filename = new File(currentMp3Path).getName();
    Mp3File file = Mp3FileDAO.findByName(filename);

    if (file != null) {
        int fileId = file.getId();
        List<Mp3Segment> segments = Mp3SegmentDAO.getByFileId(fileId);

        if (segments != null && !segments.isEmpty()) {
            tableModel.setRowCount(0);
            for (Mp3Segment seg : segments) {
                tableModel.addRow(new Object[]{seg.getStartTime(), seg.getEndTime(), seg.getText()});
            }
        } else {
            loadFromJsonAndInsert(filename);
        }
    } else {
        loadFromJsonAndInsert(filename);
    }
}

    private void loadFromJsonAndInsert(String filename) {
        JDialog loadingDialog = new JDialog(this, "Đang xử lý Whisper...", true);
        JLabel label = new JLabel("⏳ Đang trích xuất lời từ file MP3, vui lòng chờ...");
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loadingDialog.add(label);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(this);

        // Chạy Whisper ở thread phụ để không chặn giao diện
        new Thread(() -> {
            try {
                String jsonPath = currentMp3Path.replace(".mp3", ".json");
                File jsonFile = new File(jsonPath);

                if (!jsonFile.exists()) {
                    String outputDir = new File(currentMp3Path).getParent();
                    boolean success = WhisperRunner.runWhisper(currentMp3Path, outputDir);
                    if (!success || !new File(jsonPath).exists()) {
                        SwingUtilities.invokeLater(() -> {
                            loadingDialog.dispose();
                            JOptionPane.showMessageDialog(this, "Không thể tạo file JSON bằng Whisper.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        });
                        return;
                    }
                }

                Gson gson = new Gson();
                FileReader reader = new FileReader(jsonPath);
                Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
                String segmentsJson = gson.toJson(map.get("segments"));
                List<Map<String, Object>> segments = gson.fromJson(segmentsJson, new TypeToken<List<Map<String, Object>>>() {}.getType());

                Mp3File file = new Mp3File(filename, currentMp3Path, segments.size(), new java.util.Date().toString());
                Mp3FileDAO.insert(file);
                file = Mp3FileDAO.findByName(filename);
                int fileId = file.getId();

                tableModel.setRowCount(0);
                for (Map<String, Object> seg : segments) {
                    double start = (double) seg.get("start");
                    double end = (double) seg.get("end");
                    String text = (String) seg.get("text");
                    String startStr = String.format("%02d:%02d", (int)(start / 60), (int)(start % 60));
                    String endStr = String.format("%02d:%02d", (int)(end / 60), (int)(end % 60));
                    Mp3SegmentDAO.insert(new Mp3Segment(fileId, startStr, endStr, text, false));
                }

                // Hiển thị lại bảng
                List<Mp3Segment> savedSegments = Mp3SegmentDAO.getByFileId(file.getId());
                for (Mp3Segment seg : savedSegments) {
                    tableModel.addRow(new Object[]{seg.getStartTime(), seg.getEndTime(), seg.getText()});
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(this, "Lỗi khi xử lý Whisper/JSON: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE)
                );
            } finally {
                SwingUtilities.invokeLater(loadingDialog::dispose);
            }
        }).start();

        loadingDialog.setVisible(true);
    }
        

	private void saveEditedSegments() {
	    if (currentMp3Path == null) {
	    	JOptionPane.showMessageDialog(this,"Vui lòng chọn file và trích xuất mới có thể lưu", "Lỗi", JOptionPane.ERROR_MESSAGE);
	    	return;}
	    String filename = new File(currentMp3Path).getName();
	    Mp3File file = Mp3FileDAO.findByName(filename);
	    if (file == null) {
	    	return;
	    	}
	    int fileId = file.getId();
	    Mp3SegmentDAO.deleteByFileId(fileId);
	    for (int i = 0; i < tableModel.getRowCount(); i++) {
	        String start = tableModel.getValueAt(i, 0).toString();
	        String end = tableModel.getValueAt(i, 1).toString();
	        String text = tableModel.getValueAt(i, 2).toString();
	        Mp3SegmentDAO.insert(new Mp3Segment(fileId, start, end, text, false));
	    }
	    JOptionPane.showMessageDialog(this,"Lưu thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
	}
    public static void main(String[] args) {
    	System.out.println("Chương trình bắt đầu");
    	Database.createTables();

        SwingUtilities.invokeLater(() -> new gui().setVisible(true));
    }
}
