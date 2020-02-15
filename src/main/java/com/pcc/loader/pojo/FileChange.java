package com.pcc.loader.pojo;

public class FileChange {

	private String fileName;
	private int linesAdded;
	private int linesDeleted;
	
	public FileChange(String fileName,
			int linesAdded,
			int linesDeleted) {
		this.fileName = fileName;
		this.linesAdded = linesAdded;
		this.linesDeleted = linesDeleted;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getLinesAdded() {
		return linesAdded;
	}
	public void setLinesAdded(int linesAdded) {
		this.linesAdded = linesAdded;
	}
	public int getLinesDeleted() {
		return linesDeleted;
	}
	public void setLinesDeleted(int linesDeleted) {
		this.linesDeleted = linesDeleted;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + linesAdded;
		result = prime * result + linesDeleted;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileChange other = (FileChange) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (linesAdded != other.linesAdded)
			return false;
		if (linesDeleted != other.linesDeleted)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileChange [fileName=" + fileName + ", linesAdded=" + linesAdded + ", linesDeleted=" + linesDeleted
				+ "]";
	}
	
	
}
