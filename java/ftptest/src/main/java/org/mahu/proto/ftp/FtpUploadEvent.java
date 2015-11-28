package org.mahu.proto.ftp;

public class FtpUploadEvent {
	
	private String upload;
	
	public FtpUploadEvent(final String anUpload) {
		upload = anUpload;
	}

	public String getUpload() {
		return upload;
	}
	
	public String toString() {
		return "FtpUploadEvent: upload="+upload;
	}
	
}
