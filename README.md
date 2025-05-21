# 🎧 Hướng Dẫn Sử Dụng Hệ Thống Trích Lời , Cắt & Ghép File MP3 Có Tích Hợp AI (Whisper)

---

## 📦 1. Giới thiệu hệ thống

Ứng dụng được viết bằng Java Swing, cho phép người dùng:
- Chọn file MP3
- Trích xuất văn bản từ file (AI Whisper)
- Chỉnh sửa văn bản mà AI trích xuất ra
- Cắt đoạn âm thanh theo thời gian (tạo file tạm để cắt không bị trùng hoặc ghi đè)
- Sắp xếp thứ tự các đoạn
- Ghép thành 1 file hoàn chỉnh
- Lưu thông tin vào cơ sở dữ liệu SQLite
- Xoá các file tạm sau khi hoàn tất

---

## 🛠️ 2. Hướng dẫn cài đặt

### 🔧 Java:

- Cài Java JDK 8 hoặc mới hơn
- Cài IDE (Eclipse/IntelliJ)
- Thêm `ffmpeg.exe` vào PATH hoặc đặt path thủ công

### 📦 Thư viện JSON cho Java:

- Sử dụng **Gson**
- Tải `gson-2.10.1.jar` từ: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
- Thêm vào Eclipse qua: `Build Path > Add External JARs...`

### 🐍 Python:
- Cài Python 3.8 – 3.11
- Cài Whisper & PyTorch:

```bash
pip install git+https://github.com/openai/whisper.git
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
```

### 🎬 FFMPEG:
- Tải tại https://www.gyan.dev/ffmpeg
- Giải nén và thêm thư mục `bin` vào PATH
- Thêm FFM

### 🗃️ SQLite:
- Không cần cài, vì đã tích hợp sẵn với JDBC SQLite hoặc cài nếu chưa có

---

## 🖥️ 3. Giao diện chính

### Chức năng chính:
- Chọn file MP3
- Trích lời (AI)
- Cắt & ghép đoạn âm thanh (gọi giao diện cắt và ghép)
- Lưu chỉnh sửa

### Giao diện:
![image-20250518202632294](C:\Users\ADMIN\AppData\Roaming\Typora\typora-user-images\image-20250518202632294.png)

---

## ✂️ 4. Giao diện cắt & ghép file

Người dùng chọn file /thời gian bắt đầu/kết thúc để cắt nhiều đoạn. Các đoạn được thêm vào bảng:

| File | Bắt đầu | Kết thúc | ⬆️ | ⬇️ | Sửa | Xoá |

- Có thể sửa lại thời gian từng đoạn 
- Xóa đoạn không muốn cắt
- Có thể thay đổi thứ tự trước khi ghép

![image-20250518202711489](C:\Users\ADMIN\AppData\Roaming\Typora\typora-user-images\image-20250518202711489.png)

---

## 🧠 5. Trích lời bằng AI Whisper

Khi nhấn nút `Trích lời (AI)`:
- Gọi whisper từ Java bằng `ProcessBuilder`
- Kết quả lưu vào file JSON (ví dụ: EmLa.json)
- Sau đó sẽ hiển thị các câu thoại vào bảng (Start - End - Text)

Python sử dụng đoạn sau:

```python
import whisper
model = whisper.load_model("base", device="cuda")
result = model.transcribe("EmLa.mp3")

with open("EmLa.json", "w", encoding="utf-8") as f:
    import json
    json.dump(result, f, ensure_ascii=False, indent=2)
```

---

## 🧵 6. Cắt & ghép file MP3

Sau khi thêm đoạn vào bảng:

- Từng đoạn được cắt bằng lệnh:

```bash
ffmpeg -y -i input.mp3 -ss START -to END -map 0:a -c copy output.mp3
```

- Sau đó tạo `input.txt`:

```
file 'temp_segment_1.mp3'
file 'temp_segment_2.mp3'
```

- Và ghép lại:

```bash
ffmpeg -f concat -safe 0 -i input.txt -c copy merged_output.mp3
```

- File kết quả được lưu bằng `FileChooser`, và ghi log vào DB.

---

## 🧹 7. Xoá file tạm sau khi ghép

Sau khi ghép xong:

```java
for (AudioSegment seg : segmentList) {
    File temp = new File(seg.getTempOutput());
    if (temp.exists()) temp.delete();
}
```

---

## 🗄️ 8. Cơ sở dữ liệu SQLite

### Bảng `Mp3File`: lưu các file đã load
- id, file_name, file_path, duration, created_at

### Bảng `Mp3Segment`: lưu đoạn cắt
- id, mp3_file_id, start_time, end_time, text, is_verified

### Bảng `MergeFile`: lưu file đã ghép
- id, fileName, outputPath, createdAt

---

## ⚠️ 9. Lỗi thường gặp & cách xử lý

| Lỗi | Nguyên nhân | Cách xử lý |
|-----|-------------|------------|
| `NullPointerException` khi ghép | Đoạn chưa có `tempOutput` | Kiểm tra `generateTempName()` |
| `file 'null'` trong input.txt | `tempOutput` chưa được set | Đảm bảo đoạn được cắt xong trước khi ghép |
| `no such table: MergeFile` | Chưa gọi `Database.createTables()` | Gọi ở `main()` |
| `FP16 not supported on CPU` | Cảnh báo từ Whisper | Bỏ qua được |
| `Performing inference on CPU` | Có GPU nhưng chưa dùng | Thêm `device="cuda"` khi load model |

---

## 📬 Tác giả

- Sinh viên: Phạm Đức Hoài Nam
- Môn học: Java
- IDE: Eclipse