package com.example.android.thekid.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoryLog {
	private static final ArrayList<String> itsLines = new ArrayList<String>();
	private static int itsMaxRows;
	//
  private final static SimpleDateFormat theirFormatter =
    new SimpleDateFormat("H:mm:ss:SSS");	

	public static void init(final int maxRows) {
		itsMaxRows = maxRows;
	}

	public static int getCount() {
		return itsLines.size();
	}

	public static synchronized ArrayList<String> getRows() {
		return new ArrayList<String>(itsLines);
	}

	public static synchronized void add(String text) {
		itsLines.add(now()+" "+text);
		if (itsLines.size() > itsMaxRows) {
			itsLines.remove(0);
		}
	}

	public static synchronized void clearDatabase() {
		itsLines.clear();
	}
	
  private final static String now() {
    Date now = new Date();
    return theirFormatter.format(now);
  }	

}
