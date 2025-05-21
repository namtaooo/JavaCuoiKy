package model;

public class Mp3File {
	private int id;
    private String fileName;
    private String filePath;
    private double duration;
    private String createdAt;
	public Mp3File(String fileName, String filePath, double duration, String createdAt) {
		this.fileName = fileName;
		this.filePath = filePath;
		this.duration = duration;
		this.createdAt = createdAt;
	}
	public Mp3File(int id, String fileName, String filePath, double duration, String createdAt) {
        this(fileName, filePath, duration, createdAt);
        this.id = id;
    }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public double getDuration() {
		return duration;
	}
	public void setDuration(double duration) {
		this.duration = duration;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "id=" + id + ", fileName=" + fileName + ", duration=" + duration + ", createdAt=" + createdAt;
	}
    
    

}
