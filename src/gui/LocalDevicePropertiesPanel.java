package gui;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.bluetooth.BlueCoveLocalDeviceProperties;

@SuppressWarnings("serial")
public class LocalDevicePropertiesPanel extends JPanel {
	private LocalDevice device;
	private JLabel lblID, lblAddress, lblName;
	private JCheckBox chkPower, chkDiscoverable;
	private JLabel lblMaxConnections, lblMaxSrvSearches;
	private JCheckBox chkInqScanning;
	private int discoverableMode;
	
	public LocalDevicePropertiesPanel() throws BluetoothStateException {
		super();
		
		device = LocalDevice.getLocalDevice();
		
		lblID = new JLabel("Device ID: " + LocalDevice.getProperty(
				BlueCoveLocalDeviceProperties.LOCAL_DEVICE_PROPERTY_DEVICE_ID));
		lblAddress = new JLabel("Bluetooth Address: " + device.getBluetoothAddress());
		lblName = new JLabel("Friendly Name: " + device.getFriendlyName());
		chkPower = new JCheckBox("Power: ", LocalDevice.isPowerOn());
		chkPower.setEnabled(false);
		discoverableMode = device.getDiscoverable();
		switch (discoverableMode) {
			case DiscoveryAgent.GIAC:
				chkDiscoverable = new JCheckBox("Discoverable: ", true);
				break;
			case DiscoveryAgent.LIAC:
				chkDiscoverable = new JCheckBox("Discoverable: ", true);
				break;
			case DiscoveryAgent.NOT_DISCOVERABLE:
				chkDiscoverable = new JCheckBox("Discoverable: ", false);
		}
		chkDiscoverable.setEnabled(false);
		lblMaxConnections = new JLabel("Maximum Connections: " + LocalDevice.getProperty(
				"bluetooth.connected.devices.max"));
		lblMaxSrvSearches = new JLabel("Maximum Service Searches: " + LocalDevice.getProperty(
				"bluetooth.sd.trans.max"));
		chkInqScanning = new JCheckBox("Inquiry Scanning During Connection: ", 
				Boolean.parseBoolean(LocalDevice.getProperty(
						"bluetooth.connected.inquiry.scan")));
		chkInqScanning.setEnabled(false);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(lblID);
		add(lblAddress);
		add(lblName);
		add(chkPower);
		add(chkDiscoverable);
		add(lblMaxConnections);
		add(lblMaxSrvSearches);
		add(chkInqScanning);
	}
}