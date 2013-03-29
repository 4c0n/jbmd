package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JFileChooser;
//import javax.swing.filechooser.FileNameExtensionFilter;
import bluetooth.Inquirer;
import bluetooth.NoLocalDeviceException;
import data.DiscoveryRelay;
import data.DiscoveryTableModel;
import data.Distributable;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener {
	private DiscoveryPanel discoveryPanel;
	private JToolBar toolbar;
	private JButton btnOpen, btnStart, btnStop, btnDeviceProperties, btnStackProperties;
	private JProgressBar progressBar;
	private JFileChooser fileChooser;
	private Inquirer inquirer;
	private Thread inquisition;
	private DiscoveryRelay discoveryRelay;
	private FileInputStream fis;
	private Distributable distributable = null;

	public MainFrame() {
		super("Java Bluetooth Media Distributor");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initToolbar();
		
		discoveryPanel = new DiscoveryPanel();
		getContentPane().add(discoveryPanel, BorderLayout.CENTER);
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		getContentPane().add(progressBar, BorderLayout.SOUTH);

		fileChooser = new JFileChooser();
		
		setSize(500, 500);
		
		discoveryRelay = new DiscoveryRelay();
	}

	private void initToolbar() {
		btnOpen = new JButton("Open");
		btnOpen.addActionListener(this);
		btnStart = new JButton("Start");
		btnStart.addActionListener(this);
		btnStop = new JButton("Stop");
		btnStop.addActionListener(this);
		btnStop.setEnabled(false);
		btnDeviceProperties = new JButton("Device Properties");
		btnDeviceProperties.addActionListener(this);
		btnStackProperties = new JButton("Stack Properties");
		btnStackProperties.addActionListener(this);
		toolbar = new JToolBar();
		toolbar.add(btnOpen);
		toolbar.add(btnStart);
		toolbar.add(btnStop);
		toolbar.add(btnDeviceProperties);
		toolbar.add(btnStackProperties);
		getContentPane().add(toolbar, BorderLayout.NORTH);
	}

	private void open() {
		//System.out.println("Show open file dialog...");
		int state = fileChooser.showOpenDialog(this);
		switch (state) {
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.APPROVE_OPTION:
			File file = fileChooser.getSelectedFile();
			//System.out.println("Getting file...");
			try {
				fis = new FileInputStream(file);
				byte[] b = new byte[fis.available()];
				if (fis.read(b) == b.length) {
					//System.out.println("File read: " + file.getName());
				}
				distributable = new Distributable(file.getName(), b);
				discoveryRelay.setDistributable(distributable);
			} 
			catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case JFileChooser.ERROR_OPTION:
		}
	}

	private void start() {
		discoveryRelay = new DiscoveryRelay();
		discoveryRelay.setDistributable(distributable);
		try {
			inquirer = new Inquirer(discoveryRelay);
		} 
		catch (NoLocalDeviceException e) {
			JOptionPane.showMessageDialog(this, "There is no local Bluetooth device or it can't be used.", "No Local Bluetooth Device...", JOptionPane.ERROR_MESSAGE);
			return;
		}
		inquisition = new Thread(inquirer);
		inquisition.start();
		DiscoveryTableModel dtm = new DiscoveryTableModel(discoveryRelay, 100);
		discoveryPanel.setTableModel(dtm);
		dtm.start();
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		btnStop.setEnabled(true);
		btnStart.setEnabled(false);
	}

	private void stop() {
		inquisition.interrupt();
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		btnStop.setEnabled(false);
		btnStart.setEnabled(true);
	}
	
	private void deviceProperties() {
		try {
			LocalDevicePropertiesPanel pnl = new LocalDevicePropertiesPanel();
			JDialog dialog = new JDialog(this, "Device Properties...", true);
			dialog.add(pnl);
			dialog.pack();
			dialog.setVisible(true);
		} 
		catch (BluetoothStateException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			return;
		}
	}
	
	private void stackProperties() {
		JDialog dialog = new JDialog(this, "Stack Properties...", true);
		dialog.add(new StackPanel());
		dialog.pack();
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOpen) open();
		else if (e.getSource() == btnStart) start();
		else if (e.getSource() == btnStop) stop();
		else if (e.getSource() == btnDeviceProperties) deviceProperties();
		else if (e.getSource() == btnStackProperties) stackProperties();
	}
}
