package org.mahu.proto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ErrorInfos {
	@Field
	public String id;
	@Field
	public List<String> name;

	public ErrorInfos(String id, List<String> name) {
		this.id = id;
		this.name = name;
	}

	public ErrorInfos() {
	}
}