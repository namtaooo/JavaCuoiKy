package model;

public class Mp3Segment {
	private int id;
    private int mp3FileId;
    private String startTime;
    private String endTime;
    private String text;
    private boolean isVerified;
    
    public Mp3Segment(int mp3FileId, String startTime, String endTime, String text, boolean isVerified) {
        this.mp3FileId = mp3FileId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.text = text;
        this.isVerified = isVerified;
    }

    public Mp3Segment(int id, int mp3FileId, String startTime, String endTime, String text, boolean isVerified) {
        this(mp3FileId, startTime, endTime, text, isVerified);
        this.id = id;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMp3FileId() {
		return mp3FileId;
	}

	public void setMp3FileId(int mp3FileId) {
		this.mp3FileId = mp3FileId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}
	@Override
    public String toString() {
        return id + " [" + startTime + " - " + endTime + "] " + text + (isVerified ? " ✔" : " ✘");
    }
}
