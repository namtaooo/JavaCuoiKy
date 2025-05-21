package model;

import java.util.UUID;

public class AudioSegment {
	private String filepath;
	private double start;
	private double end;
	private String tempOutput;
	public AudioSegment(String filepath, double start, double end) {
		this.filepath = filepath;
		this.start = start;
		this.end = end;
		this.tempOutput = generateTempName();
	}
	private String generateTempName() {
		return "temp/temp_segment_" + UUID.randomUUID() + ".mp3";
	}
	public String getFilepath() {
		return filepath;
	}
	public double getStart() {
		return start;
	}
	public double getEnd() {
		return end;
	}
	public String getTempOutput() {
		return tempOutput;
	}
	@Override
	public String toString() {
		return "AudioSegment [filepath=" + filepath + ", start=" + start + ", end=" + end + ", tempOutput=" + tempOutput
				+ "]";
	}
	
	

}
