package com.pcc.loader.pojo;

import org.springframework.data.annotation.Id;

public class FileCommit {

	@Id
	public String id;

	private String hash;
	private String email;
	private String comment;
	private String fileName;
	private int linesAdded;
	private int linesDeleted;
	
	public FileCommit(String hash,
	String email,
	String comment,
	String fileName,
	int linesAdded,
	int linesDeleted) {
		this.hash = hash;
		this.email = email;
		this.comment = comment;
		this.fileName = fileName;
		this.linesDeleted = linesDeleted;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
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
		FileCommit other = (FileCommit) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		if (linesAdded != other.linesAdded)
			return false;
		if (linesDeleted != other.linesDeleted)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FileCommit [hash=" + hash + ", email=" + email + ", comment=" + comment + ", fileName=" + fileName
				+ ", linesAdded=" + linesAdded + ", linesDeleted=" + linesDeleted + "]";
	}
	
}
