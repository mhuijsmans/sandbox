package org.mahu.proto;

import org.apache.solr.client.solrj.beans.Field;

public class ErrorInfo {
	@Field
	public String id;
	@Field
	public String name;

	public ErrorInfo(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public ErrorInfo() {
	}
}