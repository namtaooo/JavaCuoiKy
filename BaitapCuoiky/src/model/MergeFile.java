package model;

public class MergeFile {
	private int id;
	private String filename;
	private String outputpath;
	private String createAt;
	public MergeFile() {}
	public MergeFile(String filename, String outputpath, String createAt) {
		this.filename = filename;
		this.outputpath = outputpath;
		this.createAt = createAt;
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getOutputpath() {
		return outputpath;
	}
	public void setOutputpath(String outputpath) {
		this.outputpath = outputpath;
	}
	public String getCreateAt() {
		return createAt;
	}
	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	@Override
	public String toString() {
		return "MergeFile [id=" + id + ", filename=" + filename + ", outputpath=" + outputpath + ", createAt="
				+ createAt + "]";
	}
	

}
