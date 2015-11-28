package org.mahu.proto.pngtexteditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowDestroyer extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		ApplicationProperties.saveProperties();
		System.exit(0);
	}
}