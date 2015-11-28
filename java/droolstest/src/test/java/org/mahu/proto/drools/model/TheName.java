package org.mahu.proto.drools.model;

import java.util.LinkedList;
import java.util.List;

public class TheName {

	private List<String> names = new LinkedList<>();

	public void add(final String name) {
		names.add(name);
	}

	public List<String>getNames() {
		return new LinkedList<>(names);
	}

	public void clear() {
		names.clear();
	}
}
