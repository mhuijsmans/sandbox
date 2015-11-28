package org.mahu.proto.pngtexteditor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class AttrList {
	private int keyGenerator = 0;
	private Map<Integer, String> attrs = new HashMap<Integer, String>();
	private boolean isDirty = false;

	public void add(final String attr) {
		attrs.put(new Integer(keyGenerator++), attr);
	}

	public void update(final Integer key, final String attr) {
		if (attr.isEmpty()) {
			attrs.remove(key);
		} else {
			attrs.put(key, attr);
		}
		isDirty = true;
	}

	public void removeEmptyFields() {
		Map<Integer, String> newAttrs = new HashMap<Integer, String>();
		for (Integer key : getKeys()) {
			String value = getValue(key);
			if (!value.isEmpty()) {
				newAttrs.put(key, value);
			}
		}
		isDirty |= (newAttrs.size() != attrs.size());
		attrs = newAttrs;
	}

	public void clear() {
		isDirty |= attrs.size()>0;
		attrs.clear();
	}

	public Set<Integer> getKeys() {
		return attrs.keySet();
	}

	public Collection<String> getValues() {
		return attrs.values();
	}

	public String getValue(final Integer key) {
		return attrs.get(key);
	}

	public int size() {
		return attrs.size();
	}
	
	public void clearDirty() {
		isDirty = false;
	}
	
	public boolean isDirty() {
		return isDirty;
	}

}
