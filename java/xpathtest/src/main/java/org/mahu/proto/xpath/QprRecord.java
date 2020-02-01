package org.mahu.proto.xpath;

import java.util.HashMap;
import java.util.Map;

final class QprRecord {
	
	private Map<String, String> map = new HashMap<>();

	public void add(String nodeName, String nodeValue) {
		map.put(nodeName, nodeValue);
	}

}
