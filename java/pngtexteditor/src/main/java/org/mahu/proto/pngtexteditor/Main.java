package org.mahu.proto.pngtexteditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame {

	private final static boolean DEBUG = true;

	private static final long serialVersionUID = 1L;

	private static Color BG_COLOR = new Color(0x0e5fd8);
	private final static Font font = new Font("Verdana", Font.PLAIN, 12);

	private final static String LOAD = "Load PNG";
	private final static String SAVE = "Save PNG";
	private final static String SAVEAS = "Save PNG as";
	private final static String NEW = "New PNG";
	private final static String ADD = "Add attribute";
	private final static String REFRESH = "Refresh attributes";
	private final static String HELP = "Help";

	private final static String NO_FILENAME = "<No filename set>";

	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;

	private AttrList attrs = new AttrList();
	private File file;
	private JPanel textPanel;
	private JPanel fileNamePanel;

	public Main() {
		ApplicationProperties.readProperties();
		setSize(WIDTH, HEIGHT);
		addWindowListener(new WindowDestroyer());
		setSystemLookAndfeel();
		
		setTitle("PNG Text Editor" + "");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		JPanel buttonPanel = createButtonPanel();
		contentPane.add(buttonPanel, BorderLayout.NORTH);

		textPanel = createtextPanel();
		contentPane.add(textPanel, BorderLayout.CENTER);

		JPanel p1 = new JPanel();
		p1.setBackground(BG_COLOR);
		contentPane.add(p1, BorderLayout.EAST);
		JPanel p2 = new JPanel();
		p2.setBackground(BG_COLOR);
		contentPane.add(p2, BorderLayout.WEST);

		createFilePanel();
		contentPane.add(fileNamePanel, BorderLayout.SOUTH);

		attrs.add("hio");
		attrs.add("hi");

		updateUI();
	}

	protected void updateUI() {
		addAttributes();
		updateFileName();
		pack();
		setLocationRelativeTo(null);
		repaint();
	}

	private void createFilePanel() {
		fileNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		fileNamePanel.setBackground(BG_COLOR);
	}

	protected JPanel createtextPanel() {
		JPanel textPanel = new JPanel();
		textPanel.setBackground(BG_COLOR);
		int nrOfRows = 0; // as many as needed
		int nrOfColums = 1;
		GridLayout experimentLayout = new GridLayout(nrOfRows, nrOfColums);
		textPanel.setLayout(experimentLayout);
		return textPanel;
	}

	protected void updateFileName() {
		fileNamePanel.removeAll();
		JLabel label = new JLabel("File: " + getAbsoluteFilename());
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(font);
		label.setForeground(Color.white);
		fileNamePanel.add(label);
	}

	private void addAttributes() {
		textPanel.removeAll();
		addTextField(null, TextPngUtil.SGS_MAGIX_HEADER);
		addTextField(null, getFilename());
		for (Integer key : attrs.getKeys()) {
			final String value = attrs.getValue(key);
			addTextField(key, value);
		}
	}

	protected void addTextField(Integer key, final String value) {
		JTextField textField = new JTextField(value);
		textField.setFont(font);
		if (key != null) {
			textField.addActionListener(new TextFieldListener(key));
			textField.addCaretListener(new ValueChangeListener(key));
		} else {
			textField.setEditable(false);
			textField.setBackground(Color.lightGray);
		}
		textPanel.add(textField);
	}

	protected JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(BG_COLOR);
		addButton(buttonPanel, NEW);
		addButton(buttonPanel, LOAD);
		addButton(buttonPanel, SAVE);
		addButton(buttonPanel, SAVEAS);
		addButton(buttonPanel, ADD);
		addButton(buttonPanel, REFRESH);
		addButton(buttonPanel, HELP);
		return buttonPanel;
	}

	protected void addButton(JPanel buttonPanel, String text) {
		JButton button = new JButton(text);
		button.setFont(font);
		button.addActionListener(new ButtonListener());
		buttonPanel.add(button);
	}

	private void addAttribute() {
		attrs.add("");
		updateUI();
	}

	private void refreshAttribute() {
		attrs.removeEmptyFields();
		updateUI();
	}

	private void savePng() {
		if (file == null) {
			showPopupMessage("Can not save, because no filename has been set.\nUse "
					+ SAVEAS);
			return;
		}
		if (file.exists()) {
			if (!doesUserConfirm("File already exists, overwrite?")) {
				return;
			}
		}
		TextPngUtil writer = new TextPngUtil(attrs);
		if (!writer.writeTo(file)) {
			showPopupMessage("Save failed");
		} else {
			attrs.clearDirty();
		}
	}

	private void saveAsPng() {
		File tmp = getFileToSaveTo();
		if (tmp == null) {
			return;
		}
		TextPngUtil writer = new TextPngUtil(attrs);
		if (!writer.writeTo(tmp)) {
			showPopupMessage("Save failed");
		} else {
			attrs.clearDirty();
		}
		updateFileAndLastVisitedDirectory(tmp);
		updateUI();
	}

	private void loadPng() {
		if (dataChangedAnduserPressedAbort()) {
			return;
		}
		File tmpFile = getFileToOpen();
		if (tmpFile == null) {
			return;
		}
		AttrList tmp = new AttrList();
		TextPngUtil reader = new TextPngUtil(tmp);
		if (reader.readFrom(tmpFile)) {
			attrs = tmp;
			updateFileAndLastVisitedDirectory(tmpFile);
			updateUI();
		} else {
			showPopupMessage("Failed to load text png.\nIf the png a text png?");
		}
	}

	private void newPg() {
		if (dataChangedAnduserPressedAbort()) {
			return;
		}		
		file = null;
		attrs.clear();
		updateUI();
	}

	private void help() {
		String helpText = "The PngTextEditor enables you to create, view and edit text PNGs.\n"
				+ "A text PNG has text encoded in the png pixel data.\n"
				+ "A text png contains a magic header ("
				+ TextPngUtil.SGS_MAGIX_HEADER
				+ ")\n"
				+ "and the filename as marker that the PNG is a text PNG.\n"
				+ "The PNG format is RGB with bitdepth=16\n"
				+ "The text is encoded in the R channel as US-ASCII with terminating zero.\n"
				+ "The text is encoded such that after conversion of the pixeldata\n"
				+ "to matlab planar column based, the R-area contains the text as a set of strings.";
		showPopupMessage(helpText);
	}
	
	private boolean dataChangedAnduserPressedAbort() {
		if (!attrs.isDirty()) {
			return false;
		}
		if (doesUserConfirm("Data has changed. Continue?")) {
			return false;
		}
		return true;
	}
	
	private void updateFileAndLastVisitedDirectory(File tmp) {
		file = tmp;
		if (file.isFile()) {
			ApplicationProperties.lastVisitedDirectory = file.getParentFile();
		}
	}	

	private String getFilename() {
		return (file != null) ? file.getName() : NO_FILENAME;
	}

	private String getAbsoluteFilename() {
		return (file != null) ? file.getAbsolutePath() : NO_FILENAME;
	}

	public static void main(String[] args) {
		Main app = new Main();
		app.setVisible(true);
	}

	private File getFileToOpen() {
		JFileChooser chooser = createFileChooserWithLastVisitedDirectory();
		FileFilter filter = new FileNameExtensionFilter("PNG images(*.png)",
				"png");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	private File getFileToSaveTo() {
		JFileChooser chooser =  createFileChooserWithLastVisitedDirectory();
		FileFilter filter = new FileNameExtensionFilter("PNG images(*.png)",
				"png");
		chooser.setFileFilter(filter);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;

	}
	
	private JFileChooser createFileChooserWithLastVisitedDirectory() {
		return (ApplicationProperties.lastVisitedDirectory != null) ? new JFileChooser(
				ApplicationProperties.lastVisitedDirectory) : new JFileChooser();
	}

	private void showPopupMessage(final String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	private boolean doesUserConfirm(final String msg) {
		final String title = "";
		int answer = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION);
		return (answer == JOptionPane.YES_OPTION);
	}
	
	private void setSystemLookAndfeel() {
        try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			if (actionCommand.equals(NEW))
				newPg();
			else if (actionCommand.equals(LOAD))
				loadPng();
			else if (actionCommand.equals(SAVE))
				savePng();
			else if (actionCommand.equals(SAVEAS))
				saveAsPng();
			else if (actionCommand.equals(ADD))
				addAttribute();
			else if (actionCommand.equals(REFRESH))
				refreshAttribute();
			else if (actionCommand.equals(HELP))
				help();
		}
	}

	class TextFieldListener implements ActionListener {
		private final Integer key;

		TextFieldListener(final Integer key) {
			this.key = key;
		}

		public void actionPerformed(ActionEvent e) {
			if (DEBUG) {
				System.out.println("key: " + key + ", actionPerformed");
			}
			JTextField textField = (JTextField) e.getSource();
			textField.setFont(font);
			textField.setBackground(Color.white);
			attrs.update(key, textField.getText());
			updateUI();
		}
	}

	class ValueChangeListener implements CaretListener {
		private final Integer key;

		ValueChangeListener(final Integer key) {
			this.key = key;
		}

		public void caretUpdate(CaretEvent e) {
			if (DEBUG) {
				System.out.println("key: " + key + ", propertyChange: " + e);
			}
			JTextField textField = (JTextField) e.getSource();
			textField.setBackground(Color.YELLOW);
		}

	}

}
