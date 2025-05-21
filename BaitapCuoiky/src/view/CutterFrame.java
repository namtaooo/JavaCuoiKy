package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import model.MergeFile;
import model.AudioSegment;
import database.MergeFileDAO;
import java.util.List;

public class CutterFrame extends JFrame {

    private JTextField txtFilePath;
    private JLabel lblDuration;
    private JSpinner spStartHour, spStartMin, spStartSec;
    private JSpinner spEndHour, spEndMin, spEndSec;
    private JButton btnChooseFile, btnAddSegment, btnMerge;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<AudioSegment> segmentList = new ArrayList<>();
    private double currentDuration = 0;

    public CutterFrame() {
        setTitle("MP3 Cutter & Merger");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        File tempDir = new File("temp");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
            System.out.println("📁 Đã tạo thư mục temp/");
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtFilePath = new JTextField(40);
        txtFilePath.setEditable(false);
        btnChooseFile = new JButton("Chọn file MP3");
        lblDuration = new JLabel("Thời lượng: -- giây");
        filePanel.add(btnChooseFile);
        filePanel.add(txtFilePath);
        filePanel.add(lblDuration);
        topPanel.add(filePanel, BorderLayout.NORTH);

        JPanel timePanel = new JPanel(new GridLayout(2, 1));
        timePanel.add(buildTimePanel("Thời gian bắt đầu:", true));
        timePanel.add(buildTimePanel("Thời gian kết thúc:", false));
        topPanel.add(timePanel, BorderLayout.CENTER);

        btnAddSegment = new JButton("➕ Thêm đoạn");
        topPanel.add(btnAddSegment, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{"File", "Bắt đầu (s)", "Kết thúc (s)","↑", "↓", "Sửa", "Xóa"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        table.getColumn("Sửa").setCellRenderer(new ButtonRenderer("Sửa"));
        table.getColumn("Sửa").setCellEditor(new ButtonEditor("Sửa", (row) -> {
            AudioSegment seg = segmentList.get(row);

            double s = seg.getStart(), e = seg.getEnd();
            spStartHour.setValue((int)(s / 3600));
            spStartMin.setValue((int)((s % 3600) / 60));
            spStartSec.setValue((int)(s % 60));

            spEndHour.setValue((int)(e / 3600));
            spEndMin.setValue((int)((e % 3600) / 60));
            spEndSec.setValue((int)(e % 60));
            txtFilePath.setText(seg.getFilepath());

            segmentList.remove(row);
            tableModel.removeRow(row);
        }));
        
        table.getColumn("↑").setCellRenderer(new ButtonRenderer("↑"));
        table.getColumn("↑").setCellEditor(new ButtonEditor("↑", (row) -> {
        	if (row > 0) {
        		Collections.swap(segmentList, row, row-1);
        		reloadTable();
        	}
        }));
        
        table.getColumn("↓").setCellRenderer(new ButtonRenderer("↓"));
        table.getColumn("↓").setCellEditor(new ButtonEditor("↓", (row) -> {
        	if (row < segmentList.size() -1)  {
        		Collections.swap(segmentList, row, row +1);
        		reloadTable();
        	}
        }));
        
        table.getColumn("Xóa").setCellRenderer(new ButtonRenderer("Xoá"));
        table.getColumn("Xóa").setCellEditor(new ButtonEditor("Xoá", (row) -> {
            segmentList.remove(row);
            tableModel.removeRow(row);
        }));

        btnMerge = new JButton("🚀 Cắt & Ghép");
        add(btnMerge, BorderLayout.SOUTH);
        btnChooseFile.addActionListener(e -> chooseFile());
        btnAddSegment.addActionListener(e -> addSegment());
        btnMerge.addActionListener(e -> mergeSegments());

        setVisible(true);
    }

    private JPanel buildTimePanel(String label, boolean isStart) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        JSpinner hour = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        JSpinner min = new JSpinner(new SpinnerNumberModel(0,0, 59, 1));
        JSpinner sec = new JSpinner(new SpinnerNumberModel(0,0, 59, 1));
        panel.add(hour); panel.add(new JLabel("h"));
        panel.add(min);  panel.add(new JLabel("m"));
        panel.add(sec);  panel.add(new JLabel("s"));

        if (isStart) {
            spStartHour = hour; spStartMin = min; spStartSec = sec;
        } else {
            spEndHour = hour; spEndMin = min; spEndSec = sec;
        }

        return panel;
    }

    private double getSpinnerTime(JSpinner hour, JSpinner min, JSpinner sec) {
        return (int) hour.getValue() * 3600 + (int) min.getValue() * 60 + (int) sec.getValue();
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtFilePath.setText(file.getAbsolutePath());
            currentDuration = getAudioDuration(file.getAbsolutePath());
            lblDuration.setText("Thời lượng: " + String.format("%.2f", currentDuration) + " giây");
        }
    }

    private double getAudioDuration(String filePath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("ffprobe", "-v", "error", "-show_entries",
                    "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", filePath);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            return Double.parseDouble(line.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private void reloadTable() {
        tableModel.setRowCount(0); // xoá hết

        for (AudioSegment seg : segmentList) {
            tableModel.addRow(new Object[]{
                new File(seg.getFilepath()).getName(),
                String.format("%.2f", seg.getStart()),
                String.format("%.2f", seg.getEnd()),
                "↑", "↓", "Sửa", "Xoá"
            });
        }
    }


    private void addSegment() {
        String path = txtFilePath.getText();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn file mp3 trước.");
            return;
        }

        double start = getSpinnerTime(spStartHour, spStartMin, spStartSec);
        double end = getSpinnerTime(spEndHour, spEndMin, spEndSec);

        if (end <= start) {
            JOptionPane.showMessageDialog(this, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu.");
            return;
        }

        if (end > currentDuration) {
            JOptionPane.showMessageDialog(this, "Thời gian kết thúc vượt quá độ dài file.");
            return;
        }

        AudioSegment seg = new AudioSegment(path, start, end);
        segmentList.add(seg);
        tableModel.addRow(new Object[]{
                new File(path).getName(),
                String.format("%.2f", start),
                String.format("%.2f", end),
                "↑", "↓",
                "Sửa",
                "Xoá"
        });
    }

    private void mergeSegments() {
        if (segmentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có đoạn nào để ghép.");
            return;
        }

        List<String> tempPaths = new ArrayList<>();
        for (AudioSegment seg : segmentList) {
            String output = seg.getTempOutput();
            cutMp3Segment(seg.getFilepath(), output, seg.getStart(), seg.getEnd());
            tempPaths.add(output);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file đã ghép");
        chooser.setSelectedFile(new File("merged_output.mp3"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        File outputFile = chooser.getSelectedFile();

        File listFile = new File("input.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
            for (String path : tempPaths) {
                writer.write("file '" + path + "'\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("📄 Ghi file input.txt:");
        for (String path : tempPaths) {
            System.out.println("file '" + path + "'");
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-y", "-f", "concat", "-safe", "0",
                    "-i", "input.txt", "-c", "copy", outputFile.getAbsolutePath());
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listFile.delete();

        MergeFile mf = new MergeFile(outputFile.getName(), outputFile.getAbsolutePath(),
                LocalDateTime.now().toString());
        MergeFileDAO.insert(mf);

        JOptionPane.showMessageDialog(this, "✅ Đã tạo file sthành công:\n" + outputFile.getAbsolutePath());
        if (outputFile.exists()) {
            for (AudioSegment seg : segmentList) {
                File temp = new File(seg.getTempOutput());
                if (temp.exists()) temp.delete();
            }
            segmentList.clear();
            tableModel.setRowCount(0);
        }
    }

    private void cutMp3Segment(String input, String output, double start, double end) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "ffmpeg", "-y", "-i", input,
                    "-ss", String.valueOf(start),
                    "-to", String.valueOf(end),
                    "-c", "copy", output
            );
            builder.inheritIO();
            Process process = builder.start();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
